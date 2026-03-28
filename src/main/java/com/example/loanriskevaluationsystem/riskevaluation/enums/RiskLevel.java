package com.example.loanriskevaluationsystem.riskevaluation.enums;// com.example.loanriskevaluationsystem.riskevaluation.entity

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public enum RiskLevel {

    LOW(
            0,             // minScore
            30,                     // maxScore
            "LOW RISK",             // description
            0.12,                   // baseInterestRate (12%)
            1.0,                    // approvalLimitRatio (100%)
            true,                   // autoProcessable
            true,                   // autoApprovable
            false,                  // requiresManualReview
            false                   // autoReject
    ),

    MEDIUM(
            30,                     // minScore
            60,                     // maxScore
            "MEDIUM RISK",          // description
            0.18,                   // baseInterestRate (18%)
            0.8,                    // approvalLimitRatio (80%)
            true,                   // autoProcessable
            true,                   // autoApprovable
            false,                  // requiresManualReview
            false                   // autoReject
    ),

    HIGH(
            60,                     // minScore
            80,                     // maxScore
            "HIGH RISK",            // description
            0.24,                   // baseInterestRate (24%)
            0.5,                    // approvalLimitRatio (50%)
            false,                  // autoProcessable
            false,                  // autoApprovable
            true,                   // requiresManualReview
            false                   // autoReject
    ),

    CRITICAL(
            80,                     // minScore
            100,                    // maxScore
            "CRITICAL RISK",        // description
            0.0,                    // baseInterestRate (0% - rejected)
            0.0,                    // approvalLimitRatio (0%)
            false,                  // autoProcessable
            false,                  // autoApprovable
            true,                   // requiresManualReview (for logging)
            true                    // autoReject
    );

    // Fields
    private final int minScore;
    private final int maxScore;
    private final String description;
    private final double baseInterestRate;
    private final double approvalLimitRatio;
    private final boolean autoProcessable;
    private final boolean autoApprovable;
    private final boolean requiresManualReview;
    private final boolean autoReject;

    // Constructor
    RiskLevel(int minScore, int maxScore, String description,
              double baseInterestRate, double approvalLimitRatio,
              boolean autoProcessable, boolean autoApprovable,
              boolean requiresManualReview, boolean autoReject) {
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
        this.baseInterestRate = baseInterestRate;
        this.approvalLimitRatio = approvalLimitRatio;
        this.autoProcessable = autoProcessable;
        this.autoApprovable = autoApprovable;
        this.requiresManualReview = requiresManualReview;
        this.autoReject = autoReject;
    }

    // ==================== STATIC METHODS ====================

    /**
     * Find risk level by score
     */
    public static RiskLevel fromScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Risk score must be between 0 and 100. Provided: " + score
            );
        }

        for (RiskLevel level : values()) {
            if (score >= level.minScore && score < level.maxScore) {
                return level;
            }
        }

        // For score 100 return CRITICAL
        return CRITICAL;
    }

    /**
     * Check if score is within valid range
     */
    public static boolean isValidScore(int score) {
        return score >= 0 && score <= 100;
    }

    // ==================== INSTANCE METHODS ====================

    /**
     * Can automatically decide at this level
     */
    public boolean canAutoDecide() {
        return autoProcessable;
    }

    /**
     * Can be automatically approved
     */
    public boolean canAutoApprove() {
        return autoApprovable;
    }

    /**
     * Requires manual review
     */
    public boolean isManualReviewRequired() {
        return requiresManualReview;
    }

    /**
     * Is automatically rejected
     */
    public boolean isAutoRejected() {
        return autoReject;
    }

    /**
     * Calculate maximum approved amount based on requested amount
     */
    public BigDecimal calculateMaxAmount(BigDecimal requestedAmount) {
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return requestedAmount
                .multiply(BigDecimal.valueOf(approvalLimitRatio))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Adjust interest rate based on credit score
     */
    public BigDecimal getAdjustedInterestRate(Integer creditScore) {
        if (creditScore == null) {
            creditScore = 0;
        }

        double rate = baseInterestRate;

        // Adjust based on credit score
        if (creditScore >= 750) {
            rate -= 0.02;  // 2% discount
        } else if (creditScore >= 650) {
            rate -= 0.01;  // 1% discount
        } else if (creditScore >= 550) {
            // Standard rate
        } else if (creditScore >= 450) {
            rate += 0.03;  // 3% increase
        } else {
            rate += 0.05;  // 5% increase
        }

        // Minimum 5% interest
        rate = Math.max(0.05, rate);

        // For HIGH and CRITICAL minimum 15%
        if ((this == HIGH || this == CRITICAL) && rate < 0.15) {
            rate = 0.15;
        }

        return BigDecimal.valueOf(rate).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Determine decision based on risk level
     */
    public RiskDecision determineDecision() {
        if (autoReject) {
            return RiskDecision.REJECT;
        }
        if (requiresManualReview) {
            return RiskDecision.MANUAL_REVIEW;
        }
        if (autoApprovable) {
            return RiskDecision.APPROVE;
        }
        return RiskDecision.MANUAL_REVIEW;
    }

    /**
     * Get rejection reasons if applicable
     */
    public String getRejectionReasons(int riskScore, BigDecimal dtiRatio) {
        if (!autoReject && !requiresManualReview) {
            return null;
        }

        StringBuilder reasons = new StringBuilder();

        if (autoReject) {
            reasons.append("Critical risk score: ").append(riskScore).append("; ");
        }

        if (requiresManualReview) {
            reasons.append("High risk level requires manual review; ");
        }

        if (dtiRatio != null && dtiRatio.compareTo(new BigDecimal("50")) > 0) {
            reasons.append("Debt-to-income ratio too high: ").append(dtiRatio).append("%; ");
        }

        return reasons.toString().trim();
    }

    /**
     * Check if score falls within this level
     */
    public boolean matchesScore(int score) {
        return score >= minScore && score < maxScore;
    }

    /**
     * Get next higher risk level
     */
    public RiskLevel getNextHigherLevel() {
        return switch (this) {
            case LOW -> MEDIUM;
            case MEDIUM -> HIGH;
            case HIGH, CRITICAL -> CRITICAL;
        };
    }

    /**
     * Get next lower risk level
     */
    public RiskLevel getNextLowerLevel() {
        return switch (this) {
            case LOW -> LOW;
            case MEDIUM -> LOW;
            case HIGH -> MEDIUM;
            case CRITICAL -> HIGH;
        };
    }

    public String getFormattedDescription() {
        return String.format("%s[%d-%d]: %s",
                name(), minScore, maxScore, description);
    }
}