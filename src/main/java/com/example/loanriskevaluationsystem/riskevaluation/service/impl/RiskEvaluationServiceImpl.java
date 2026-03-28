package com.example.loanriskevaluationsystem.riskevaluation.service.impl;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.loanapp.repository.LoanApplicationRepository;
import com.example.loanriskevaluationsystem.riskevaluation.dto.request.RiskEvaluationRequest;
import com.example.loanriskevaluationsystem.riskevaluation.dto.response.RiskEvaluationResponse;
import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskDecision;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskLevel;
import com.example.loanriskevaluationsystem.riskevaluation.mapper.RiskEvaluationMapper;
import com.example.loanriskevaluationsystem.riskevaluation.repository.RiskEvaluationRepository;
import com.example.loanriskevaluationsystem.riskevaluation.service.RiskEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.loanriskevaluationsystem.shared.event.RiskEvaluationResultEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskEvaluationServiceImpl implements RiskEvaluationService {
    private final RiskEvaluationRepository repository;
    private final LoanApplicationRepository loanRepository;
    private final RiskEvaluationMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public RiskEvaluationResponse evaluateRisk(Long loanApplicationId) {
        log.info("Evaluating risk for loan: {}", loanApplicationId);


        LoanApplication loan = loanRepository.findById(loanApplicationId)
                .orElseThrow(() -> new RuntimeException("LoanApplication not found: " + loanApplicationId));


        // Calculate risk score
        int riskScore = calculateRiskScore(loan);

        // Determine risk level
        RiskLevel level = RiskLevel.fromScore(riskScore);

        // Calculate DTI ratio
        BigDecimal dtiRatio = calculateDTI(loan);

        // Get decision
        RiskDecision decision = determineDecision(level, dtiRatio);

        // Calculate amounts
        BigDecimal maxAmount = calculateMaxAmount(level, loan.getRequestedAmount());
        BigDecimal interestRate = calculateInterestRate(level, loan.getCreditScore());

        // Build evaluation
        RiskEvaluation evaluation = RiskEvaluation.builder()
                .loanApplication(loan)
                .riskScore(riskScore)
                .riskLevel(level)
                .decision(decision)
                .debtToIncomeRatio(dtiRatio)
                .maxApprovedAmount(maxAmount)
                .suggestedInterestRate(interestRate)
                .rejectionReasons(generateRejectionReasons(level, dtiRatio))
                .evaluatedBy("SYSTEM")
                .build();

        RiskEvaluation saved = repository.save(evaluation);

        // Update loan status
        updateLoanStatus(loan, decision);

        log.info("Risk evaluation completed for {}: {} - {}",
                loanApplicationId, level, decision);

        // Build Event and send as JSON
        try {
            RiskEvaluationResultEvent event = RiskEvaluationResultEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .loanApplicationId(String.valueOf(loanApplicationId))
                    .riskEvaluationId(String.valueOf(saved.getId()))
                    .riskScore(saved.getRiskScore())
                    .riskLevel(saved.getRiskLevel().name())
                    .decision(saved.getDecision().name())
                    .maxApprovedAmount(saved.getMaxApprovedAmount())
                    .suggestedInterestRate(saved.getSuggestedInterestRate())
                    .evaluatedAt(saved.getEvaluatedAt())
                    .build();
            
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(
                    "risk.exchange",
                    "risk.result",
                    json
            );
            log.info("Risk result sent to RabbitMQ for loan: {}", loanApplicationId);
        } catch (Exception e) {
            log.error("Failed to serialize and send result event", e);
        }
        return mapper.toResponse(saved);
    }

    private int calculateRiskScore(LoanApplication loan) {
        int score = 50; // Base score

        // Credit score factor
        Integer creditScore = loan.getCreditScore();
        if (creditScore != null) {
            if (creditScore >= 750) score -= 20;
            else if (creditScore >= 650) score -= 10;
            else if (creditScore >= 550) score += 10;
            else score += 30;
        }

        // DTI factor
        BigDecimal dti = calculateDTI(loan);
        if (dti.compareTo(new BigDecimal("20")) < 0) score -= 10;
        else if (dti.compareTo(new BigDecimal("40")) > 0) score += 20;
        else if (dti.compareTo(new BigDecimal("50")) > 0) score += 40;

        // Amount to income ratio
        BigDecimal amountIncomeRatio = loan.getRequestedAmount()
                .divide(loan.getMonthlyIncome(), 2, RoundingMode.HALF_UP);
        if (amountIncomeRatio.compareTo(new BigDecimal("10")) > 0) score += 15;

        return Math.max(0, Math.min(100, score));
    }

    private BigDecimal calculateDTI(LoanApplication loan) {
        if (loan.getMonthlyIncome() == null ||
                loan.getMonthlyIncome().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal monthlyDebt = loan.getExistingDebtAmount() != null
                ? loan.getExistingDebtAmount().divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return monthlyDebt
                .divide(loan.getMonthlyIncome(), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    private RiskDecision determineDecision(RiskLevel level, BigDecimal dtiRatio) {
        if (level.isAutoRejected() || dtiRatio.compareTo(new BigDecimal("60")) > 0) {
            return RiskDecision.REJECT;
        }
        if (level.isManualReviewRequired()) {
            return RiskDecision.MANUAL_REVIEW;
        }
        return RiskDecision.APPROVE;
    }

    @Override
    public BigDecimal calculateMaxAmount(RiskLevel level, BigDecimal requestedAmount) {
        return level.calculateMaxAmount(requestedAmount);
    }

    @Override
    public BigDecimal calculateInterestRate(RiskLevel level, Integer creditScore) {
        return level.getAdjustedInterestRate(creditScore);
    }

    private String generateRejectionReasons(RiskLevel level, BigDecimal dtiRatio) {
        if (!level.isAutoRejected() && !level.isManualReviewRequired()) {
            return null;
        }

        StringBuilder reasons = new StringBuilder();
        if (level.isAutoRejected()) {
            reasons.append("Critical risk level; ");
        }
        if (level.isManualReviewRequired()) {
            reasons.append("Manual review required; ");
        }
        if (dtiRatio.compareTo(new BigDecimal("50")) > 0) {
            reasons.append("High debt-to-income ratio: ").append(dtiRatio).append("%; ");
        }

        return reasons.toString().trim();
    }

    private void updateLoanStatus(LoanApplication loan, RiskDecision decision) {
        switch (decision) {
            case APPROVE -> loan.setStatus(com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.APPROVED);
            case REJECT -> loan.setStatus(com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.REJECTED);
            case MANUAL_REVIEW -> loan.setStatus(com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.UNDER_REVIEW);
        }
        loanRepository.save(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskEvaluationResponse getEvaluation(Long id) {
        RiskEvaluation evaluation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found: " + id));
        return mapper.toResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskEvaluationResponse getEvaluationByLoanId(Long loanApplicationId) {
        RiskEvaluation evaluation = repository.findByLoanApplicationId(loanApplicationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found for loan: " + loanApplicationId));
        return mapper.toResponse(evaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskEvaluationResponse> getAllEvaluations() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskEvaluationResponse> getEvaluationsByLevel(RiskLevel level) {
        return repository.findByRiskLevel(level).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskEvaluationResponse> getEvaluationsByDecision(RiskDecision decision) {
        return repository.findByDecision(decision).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processRiskEvaluation(RiskEvaluationRequest request) {
        evaluateRisk(Long.valueOf(request.getLoanApplicationId()));
    }

    public void sendRiskEvaluation(RiskEvaluationResponse response) {
        rabbitTemplate.convertAndSend("risk.result.queue", response);
    }
}
