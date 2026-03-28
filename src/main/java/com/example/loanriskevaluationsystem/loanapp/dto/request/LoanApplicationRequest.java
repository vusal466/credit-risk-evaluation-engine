package com.example.loanriskevaluationsystem.loanapp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Loan application request")
public class LoanApplicationRequest {
    @Schema(description = "Customer ID", example = "CUST123456", required = true)
    @NotNull(message = "Customer ID is required")
    @com.fasterxml.jackson.annotation.JsonProperty("customerId")
    private Long customerId;

    @Schema(description = "Customer full name", example = "John Doe", required = true)
    @NotBlank(message = "Customer name is required")
    @com.fasterxml.jackson.annotation.JsonProperty("customerName")
    private String customerName;

    @Schema(description = "Requested loan amount", example = "10000.00", required = true)
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @com.fasterxml.jackson.annotation.JsonProperty("requestedAmount")
    private BigDecimal requestedAmount;

    @Schema(description = "Loan purpose", example = "HOME")
    @com.fasterxml.jackson.annotation.JsonProperty("loanPurpose")
    private String loanPurpose;

    @Schema(description = "Loan term in months", example = "24", required = true)
    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Minimum 1 month")
    @Max(value = 360, message = "Maximum 360 months")
    @com.fasterxml.jackson.annotation.JsonProperty("loanTermMonths")
    private Integer loanTermMonths;

    @Schema(description = "Monthly income", example = "2500.00", required = true)
    @NotNull(message = "Income is required")
    @Positive(message = "Income must be positive")
    @com.fasterxml.jackson.annotation.JsonProperty("monthlyIncome")
    private BigDecimal monthlyIncome;

    @Schema(description = "Employment type", example = "SALARIED")
    @com.fasterxml.jackson.annotation.JsonProperty("employmentType")
    private String employmentType;

    @Schema(description = "Credit score (300-850)", example = "720")
    @Min(value = 300, message = "Minimum 300")
    @Max(value = 850, message = "Maximum 850")
    @com.fasterxml.jackson.annotation.JsonProperty("creditScore")
    private Integer creditScore;

    @Schema(description = "Existing debt amount", example = "5000.00")
    @com.fasterxml.jackson.annotation.JsonProperty("existingDebtAmount")
    private BigDecimal existingDebtAmount;
}
