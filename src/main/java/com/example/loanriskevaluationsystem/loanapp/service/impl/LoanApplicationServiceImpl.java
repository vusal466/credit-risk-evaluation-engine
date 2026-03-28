package com.example.loanriskevaluationsystem.loanapp.service.impl;


import com.example.loanriskevaluationsystem.loanapp.dto.RiskSummaryDTO;
import com.example.loanriskevaluationsystem.loanapp.dto.request.LoanApplicationRequest;
import com.example.loanriskevaluationsystem.loanapp.dto.response.LoanApplicationResponse;
import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;

import com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus;
import com.example.loanriskevaluationsystem.loanapp.mapper.LoanApplicationMapper;
import com.example.loanriskevaluationsystem.loanapp.messaging.LoanApplicationPublisher;
import com.example.loanriskevaluationsystem.loanapp.repository.LoanApplicationRepository;
import com.example.loanriskevaluationsystem.loanapp.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final LoanApplicationMapper mapper;
    private final LoanApplicationPublisher publisher;

    @Override
    @Transactional
    public LoanApplicationResponse createApplication(LoanApplicationRequest request) {
        log.info("Creating loan application for customer: {}", request.getCustomerId());

        LoanApplication entity = mapper.toEntity(request);
        LoanApplication saved = repository.save(entity);

        log.info("Loan application created with ID: {}", saved.getId());
        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanApplicationResponse getApplication(Long id) {
        LoanApplication entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan application not found: " + id));
        return buildResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getAllApplications() {
        return repository.findAll().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getApplicationsByCustomer(Long customerId) {
        return repository.findByCustomerId(String.valueOf(customerId)).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getApplicationsByStatus(LoanStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanApplicationResponse updateApplication(Long id, LoanApplicationRequest request) {
        LoanApplication existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan application not found: " + id));

        mapper.updateEntity(existing, request);
        LoanApplication updated = repository.save(existing);

        log.info("Loan application updated: {}", id);
        return buildResponse(updated);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        repository.deleteById(id);
        log.info("Loan application deleted: {}", id);
    }

    @Override
    @Transactional
    public void submitForRiskEvaluation(Long loanId) {
        LoanApplication loan = repository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan application not found: " + loanId));

        loan.setStatus(LoanStatus.UNDER_REVIEW);
        repository.save(loan);

        publisher.publish(loan);
        log.info("Loan application submitted for risk evaluation: {}", loanId);
    }

    @Override
    @Transactional
    public void updateStatus(Long loanId, LoanStatus status) {
        LoanApplication loan = repository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan application not found: " + loanId));

        loan.setStatus(status);
        repository.save(loan);
        log.info("Loan application status updated to {}: {}", status, loanId);
    }

    // Manual response builder
    private LoanApplicationResponse buildResponse(LoanApplication entity) {
        return LoanApplicationResponse.builder()
                .id(entity.getId())
                .customerId(Long.valueOf(entity.getCustomerId()))
                .customerName(entity.getCustomerName())
                .requestedAmount(entity.getRequestedAmount())
                .loanPurpose(entity.getLoanPurpose())
                .loanTermMonths(entity.getLoanTermMonths())
                .monthlyIncome(entity.getMonthlyIncome())
                .employmentType(entity.getEmploymentType() != null ? entity.getEmploymentType().name() : null)
                .creditScore(entity.getCreditScore())
                .existingDebtAmount(entity.getExistingDebtAmount())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .applicationDate(entity.getApplicationDate())
                .riskSummary(buildRiskSummary(entity))
                .build();
    }

    private RiskSummaryDTO buildRiskSummary(LoanApplication entity) {
        if (entity.getRiskEvaluation() == null) {
            return null;
        }

        return RiskSummaryDTO.builder()
                .riskLevel(entity.getRiskEvaluation().getRiskLevel() != null ?
                        entity.getRiskEvaluation().getRiskLevel().name() : null)
                .decision(entity.getRiskEvaluation().getDecision() != null ?
                        entity.getRiskEvaluation().getDecision().name() : null)
                .riskScore(entity.getRiskEvaluation().getRiskScore())
                .build();
    }
}