package com.example.loanriskevaluationsystem.loanapp.entity;

import com.example.loanriskevaluationsystem.loanapp.enums.EmploymentType;
import com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus;
import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loan_application")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq")
    @SequenceGenerator(
            name = "loan_seq",
            sequenceName = "loan_application_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "loan_purpose")
    private String loanPurpose;

    @Column(name = "loan_term_months", nullable = false)
    private Integer loanTermMonths;

    @Column(name = "monthly_income", nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(nullable = false)
    private Integer creditScore;

    @Column(name = "existing_debt_amount", precision = 19, scale = 2)
    private BigDecimal existingDebtAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @OneToOne(mappedBy = "loanApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RiskEvaluation riskEvaluation;


    @PrePersist
    protected void onCreate() {
        this.applicationDate = LocalDateTime.now();
        this.status = LoanStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
