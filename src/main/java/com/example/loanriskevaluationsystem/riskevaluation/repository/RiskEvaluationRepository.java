package com.example.loanriskevaluationsystem.riskevaluation.repository;

import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskDecision;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskEvaluationRepository extends JpaRepository<RiskEvaluation,Long> {

    // Find by loan application ID
    Optional<RiskEvaluation> findByLoanApplicationId(Long loanApplicationId);

    // Find by risk level
    List<RiskEvaluation> findByRiskLevel(RiskLevel riskLevel);

    // Find by decision
    List<RiskEvaluation> findByDecision(RiskDecision decision);

    // Find high risk evaluations
    List<RiskEvaluation> findByRiskScoreGreaterThanEqual(Integer score);

    // Find by date range
    List<RiskEvaluation> findByEvaluatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Find approved applications with specific interest rate range
    List<RiskEvaluation> findByDecisionAndSuggestedInterestRateBetween(
            RiskDecision decision,
            BigDecimal minRate,
            BigDecimal maxRate
    );

    // Custom query: Find by loan application customer ID
    @Query("SELECT r FROM RiskEvaluation r WHERE r.loanApplication.customerId = :customerId")
    Optional<RiskEvaluation> findByCustomerId(@Param("customerId") String customerId);

    // Custom query: Average risk score by decision
    @Query("SELECT r.decision, AVG(r.riskScore) FROM RiskEvaluation r GROUP BY r.decision")
    List<Object[]> averageRiskScoreByDecision();

    // Custom query: Count by risk level
    @Query("SELECT r.riskLevel, COUNT(r) FROM RiskEvaluation r GROUP BY r.riskLevel")
    List<Object[]> countByRiskLevel();


    // Find evaluations with loan application details
    @Query("SELECT r FROM RiskEvaluation r JOIN FETCH r.loanApplication WHERE r.id = :id")
    Optional<RiskEvaluation> findByIdWithLoanApplication(@Param("id") Long id);

    // Find recent evaluations (last 24 hours)
    @Query("SELECT r FROM RiskEvaluation r WHERE r.evaluatedAt > :time")
    List<RiskEvaluation> findRecentEvaluations(@Param("time") LocalDateTime time);

    // Find by max approved amount greater than
    List<RiskEvaluation> findByMaxApprovedAmountGreaterThan(BigDecimal amount);
}
