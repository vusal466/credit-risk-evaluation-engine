package com.example.loanriskevaluationsystem.loanapp.dto.response;

import com.example.loanriskevaluationsystem.loanapp.dto.RiskSummaryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Loan application response")
public class LoanApplicationResponse {

    @Schema(description = "Loan application ID", example = "LOAN-abc123")
    private Long id;

    @Schema(description = "Customer ID", example = "CUST123456")
    private Long customerId;

    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;

    @Schema(description = "Requested amount", example = "10000.00")
    private BigDecimal requestedAmount;

    @Schema(description = "Loan purpose", example = "HOME")
    private String loanPurpose;

    @Schema(description = "Loan term in months", example = "24")
    private Integer loanTermMonths;

    @Schema(description = "Monthly income", example = "2500.00")
    private BigDecimal monthlyIncome;

    @Schema(description = "Employment type", example = "SALARIED")
    private String employmentType;

    @Schema(description = "Credit score", example = "720")
    private Integer creditScore;

    @Schema(description = "Existing debt", example = "5000.00")
    private BigDecimal existingDebtAmount;

    @Schema(description = "Application status", example = "PENDING")
    private String status;

    @Schema(description = "Application date", example = "2026-01-01T10:30:00")
    private LocalDateTime applicationDate;

    @Schema(description = "Risk evaluation summary")
    private RiskSummaryDTO riskSummary;
}
