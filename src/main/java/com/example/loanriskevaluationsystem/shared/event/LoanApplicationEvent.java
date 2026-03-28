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
public class LoanApplicationEvent implements Serializable {

    private String eventId;
    private String loanApplicationId;
    private String customerId;
    private BigDecimal requestedAmount;
    private Integer creditScore;
    private BigDecimal monthlyIncome;
    private BigDecimal existingDebt;
    private LocalDateTime timestamp;
    private EventType eventType;

    public enum EventType {
        APPLICATION_SUBMITTED,
        RISK_EVALUATION_REQUESTED,
        RISK_EVALUATION_COMPLETED
    }
}