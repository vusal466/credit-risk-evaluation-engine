package com.example.loanriskevaluationsystem.loanapp.service;

import com.example.loanriskevaluationsystem.loanapp.dto.request.LoanApplicationRequest;
import com.example.loanriskevaluationsystem.loanapp.dto.response.LoanApplicationResponse;
import com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus;

import java.util.List;

public interface LoanApplicationService {

    LoanApplicationResponse createApplication(LoanApplicationRequest request);

    LoanApplicationResponse getApplication(Long id);

    List<LoanApplicationResponse> getAllApplications();

    List<LoanApplicationResponse> getApplicationsByCustomer(Long customerId);

    List<LoanApplicationResponse> getApplicationsByStatus(LoanStatus status);

    LoanApplicationResponse updateApplication(Long id, LoanApplicationRequest request);

    void deleteApplication(Long id);

    void submitForRiskEvaluation(Long loanId);

    void updateStatus(Long loanId, LoanStatus status);

}
