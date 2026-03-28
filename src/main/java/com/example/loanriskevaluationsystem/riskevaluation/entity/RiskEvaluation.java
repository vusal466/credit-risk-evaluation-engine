package com.example.loanriskevaluationsystem.riskevaluation.entity;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskDecision;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private RiskDecision decision;

    @Column(name = "debt_to_income_ratio", precision = 5, scale = 2)
    private BigDecimal debtToIncomeRatio;

    @Column(name = "max_approved_amount", precision = 19, scale = 2)
    private BigDecimal maxApprovedAmount;

    @Column(name = "suggested_interest_rate", precision = 5, scale = 4)
    private BigDecimal suggestedInterestRate;

    @Column(name = "rejection_reasons", length = 1000)
    private String rejectionReasons;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "evaluated_by", length = 50)
    private String evaluatedBy;

    @PrePersist
    protected void onCreate() {
        this.evaluatedAt = LocalDateTime.now();
    }
}