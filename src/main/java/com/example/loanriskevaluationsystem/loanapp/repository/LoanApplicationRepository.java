package com.example.loanriskevaluationsystem.loanapp.repository;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication,Long> {

    // Find by customer ID
    List<LoanApplication> findByCustomerId(String customerId);

    // Find by status
    List<LoanApplication> findByStatus(LoanStatus status);

    // Find by customer and status
    List<LoanApplication> findByCustomerIdAndStatus(String customerId, LoanStatus status);

    // Find pending applications (for risk evaluation queue)
    List<LoanApplication> findByStatusOrderByApplicationDateAsc(LoanStatus status);

    // Find applications by date range
    List<LoanApplication> findByApplicationDateBetween(LocalDateTime start, LocalDateTime end);


    // Find by minimum amount
    List<LoanApplication> findByRequestedAmountGreaterThan(BigDecimal amount);

    // Find by customer name
    List<LoanApplication> findByCustomerNameContainingIgnoreCase(String customerName);

    // Check if customer has any active loans
    boolean existsByCustomerIdAndStatusIn(String customerId, List<LoanStatus> statuses);

    // Custom query: Find high value applications
    @Query("SELECT l FROM LoanApplication l WHERE l.requestedAmount > :amount AND l.status = :status")
    List<LoanApplication> findHighValueApplications(
            @Param("amount") BigDecimal amount,
            @Param("status") LoanStatus status
    );

    // Custom query: Count applications by status
    @Query("SELECT l.status, COUNT(l) FROM LoanApplication l GROUP BY l.status")
    List<Object[]> countByStatus();

    // Find application with risk evaluation (fetch join)
    @Query("SELECT l FROM LoanApplication l LEFT JOIN FETCH l.riskEvaluation WHERE l.id = :id")
    Optional<LoanApplication> findByIdWithRiskEvaluation(@Param("id") Long id);

    // Find applications submitted today
    @Query("""
    SELECT l FROM LoanApplication l 
    WHERE l.applicationDate >= :start 
      AND l.applicationDate < :end
""")
    List<LoanApplication> findTodayApplications(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
