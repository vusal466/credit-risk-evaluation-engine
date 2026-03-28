package com.example.loanriskevaluationsystem.riskevaluation.controller;

import com.example.loanriskevaluationsystem.riskevaluation.dto.response.RiskEvaluationResponse;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskDecision;
import com.example.loanriskevaluationsystem.riskevaluation.enums.RiskLevel;
import com.example.loanriskevaluationsystem.riskevaluation.service.RiskEvaluationService;
import com.example.loanriskevaluationsystem.shared.dto.ApidtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
@Tag(name = "Risk Evaluation", description = "Risk evaluation management APIs")

public class RiskEvaluationController {

    private final RiskEvaluationService service;

    @Operation(
            summary = "Evaluate loan risk",
            description = "Performs risk evaluation for a loan application"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluation completed"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "500", description = "Evaluation failed")
    })
    @PostMapping("/evaluate/{loanId}")
    public ResponseEntity<ApidtoResponse<RiskEvaluationResponse>> evaluate(
            @Parameter(description = "Loan application ID") @PathVariable String loanId) {

        RiskEvaluationResponse response = service.evaluateRisk(Long.valueOf(loanId));
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get evaluation by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApidtoResponse<RiskEvaluationResponse>> getById(
            @Parameter(description = "Evaluation ID") @PathVariable String id) {

        RiskEvaluationResponse response = service.getEvaluation(Long.valueOf(id));
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get evaluation by loan ID")
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<ApidtoResponse<RiskEvaluationResponse>> getByLoanId(
            @Parameter(description = "Loan application ID") @PathVariable String loanId){

        RiskEvaluationResponse response = service.getEvaluationByLoanId(Long.valueOf(loanId));
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get all evaluations")
    @GetMapping
    public ResponseEntity<ApidtoResponse<List<RiskEvaluationResponse>>> getAll() {

        List<RiskEvaluationResponse> response = service.getAllEvaluations();
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get evaluations by risk level")
    @GetMapping("/level/{level}")
    public ResponseEntity<ApidtoResponse<List<RiskEvaluationResponse>>> getByLevel(
            @Parameter(description = "Risk level") @PathVariable RiskLevel level) {

        List<RiskEvaluationResponse> response = service.getEvaluationsByLevel(level);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get evaluations by decision")
    @GetMapping("/decision/{decision}")
    public ResponseEntity<ApidtoResponse<List<RiskEvaluationResponse>>> getByDecision(
            @Parameter(description = "Risk decision") @PathVariable RiskDecision decision) {

        List<RiskEvaluationResponse> response = service.getEvaluationsByDecision(decision);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }
}