package com.example.loanriskevaluationsystem.loanapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskSummaryDTO {
    private String riskLevel;
    private String decision;
    private Integer riskScore;
}
