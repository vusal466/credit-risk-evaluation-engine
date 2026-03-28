package com.example.loanriskevaluationsystem.loanapp.controller;

import com.example.loanriskevaluationsystem.loanapp.dto.request.LoanApplicationRequest;
import com.example.loanriskevaluationsystem.loanapp.dto.response.LoanApplicationResponse;
import com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus;
import com.example.loanriskevaluationsystem.loanapp.service.LoanApplicationService;

import com.example.loanriskevaluationsystem.shared.dto.ApidtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Application", description = "Loan application management APIs")
public class LoanApplicationController {

    private final LoanApplicationService service;

    @Operation(
            summary = "Create new loan application",
            description = "Creates a new loan application for a customer"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping
    public ResponseEntity<ApidtoResponse<LoanApplicationResponse>> create(
            @Valid @RequestBody @Parameter(description = "Loan application details") LoanApplicationRequest request) {

        LoanApplicationResponse response = service.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApidtoResponse<LoanApplicationResponse>> getById(@Parameter(description = "Loan ID") @PathVariable Long id) {
        LoanApplicationResponse response = service.getApplication(id);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get all loans")
    @GetMapping
    public ResponseEntity<ApidtoResponse<List<LoanApplicationResponse>>> getAll() {
        List<LoanApplicationResponse> response = service.getAllApplications();
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get loans by customer ID")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApidtoResponse<List<LoanApplicationResponse>>> getByCustomer(  @Parameter(description = "Customer ID")
            @PathVariable Long customerId) {

        List<LoanApplicationResponse> response = service.getApplicationsByCustomer(customerId);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Get loans by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApidtoResponse<List<LoanApplicationResponse>>> getByStatus(
            @Parameter(description = "Loan status") @PathVariable LoanStatus status) {

        List<LoanApplicationResponse> response = service.getApplicationsByStatus(status);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Update loan application")
    @PutMapping("/{id}")
    public ResponseEntity<ApidtoResponse<LoanApplicationResponse>> update(
            @Parameter(description = "Loan ID") @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequest request) {

        LoanApplicationResponse response = service.updateApplication(id, request);
        return ResponseEntity.ok(ApidtoResponse.success(response));
    }

    @Operation(summary = "Delete loan application")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApidtoResponse<Void>> delete(@Parameter(description = "Loan ID")
                                                           @PathVariable Long id) {
        service.deleteApplication(id);
        return ResponseEntity.ok(ApidtoResponse.success(null));
    }

    @Operation(
            summary = "Submit for risk evaluation",
            description = "Sends loan application to risk evaluation queue"
    )
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApidtoResponse<Void>> submitForEvaluation(@Parameter(description = "Loan ID")
                                                                        @PathVariable Long id) {
        service.submitForRiskEvaluation(id);
        return ResponseEntity.ok(ApidtoResponse.success(null));
    }

    @RabbitListener(queues = "test-queue")
    public void receive(String message) {
        System.out.println("Received: " + message);
    }

    @Operation(summary = "Update loan status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApidtoResponse<Void>> updateStatus(
            @Parameter(description = "Loan ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam LoanStatus status) {

        service.updateStatus(id, status);
        return ResponseEntity.ok(ApidtoResponse.success(null));
    }
}