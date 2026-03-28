package com.example.loanriskevaluationsystem.shared.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEvaluationResultEvent implements Serializable {

    private String eventId;
    private String loanApplicationId;
    private String riskEvaluationId;
    private Integer riskScore;
    private String riskLevel;
    private String decision;
    private BigDecimal maxApprovedAmount;
    private BigDecimal suggestedInterestRate;
    private LocalDateTime evaluatedAt;
}