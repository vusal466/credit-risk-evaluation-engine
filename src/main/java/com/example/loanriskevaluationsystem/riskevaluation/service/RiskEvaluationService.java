package com.example.loanriskevaluationsystem.riskevaluation.service;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.riskevaluation.dto.request.RiskEvaluationRequest;
import com.example.loanriskevaluationsystem.riskevaluation.dto.response.RiskEvaluationResponse;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskDecision;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskLevel;

import java.math.BigDecimal;
import java.util.List;

public interface RiskEvaluationService {
    RiskEvaluationResponse evaluateRisk(Long loanApplicationId);

    RiskEvaluationResponse getEvaluation(Long id);

    RiskEvaluationResponse getEvaluationByLoanId(Long loanApplicationId);

    List<RiskEvaluationResponse> getAllEvaluations();

    List<RiskEvaluationResponse> getEvaluationsByLevel(RiskLevel level);

    List<RiskEvaluationResponse> getEvaluationsByDecision(RiskDecision decision);

    void processRiskEvaluation(RiskEvaluationRequest request);

    BigDecimal calculateMaxAmount(RiskLevel level, BigDecimal requestedAmount);

    BigDecimal calculateInterestRate(RiskLevel level, Integer creditScore);
}
