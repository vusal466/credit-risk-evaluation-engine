package com.example.loanriskevaluationsystem.riskevaluation.dto.response;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RiskEvaluationResponse  implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer riskScore;
    private String riskLevel;
    private String decision;
    private BigDecimal debtToIncomeRatio;
    private BigDecimal maxApprovedAmount;
    private BigDecimal suggestedInterestRate;
    private String rejectionReasons;
    private LocalDateTime evaluatedAt;
    private String evaluatedBy;

    private Long loanApplicationId;
    private String customerId;
    private BigDecimal requestedAmount;

    private RiskEvaluationResponse buildResponse(RiskEvaluation entity, LoanApplication loan) {
        return RiskEvaluationResponse.builder()
                .id(Long.valueOf(String.valueOf(entity.getId())))
                .riskScore(entity.getRiskScore())
                .riskLevel(entity.getRiskLevel() != null ? entity.getRiskLevel().name() : null)
                .decision(entity.getDecision() != null ? entity.getDecision().name() : null)
                .debtToIncomeRatio(entity.getDebtToIncomeRatio())
                .maxApprovedAmount(entity.getMaxApprovedAmount())
                .suggestedInterestRate(entity.getSuggestedInterestRate())
                .rejectionReasons(entity.getRejectionReasons())
                .evaluatedAt(entity.getEvaluatedAt())
                .evaluatedBy(entity.getEvaluatedBy())
                // Loan info
                .loanApplicationId(loan != null ? loan.getId() : null)
                .customerId(loan != null ? loan.getCustomerId() : null)
                .requestedAmount(loan != null ? loan.getRequestedAmount() : null)
                .build();
    }

}