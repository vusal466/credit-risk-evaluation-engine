package com.example.loanriskevaluationsystem.shared.messaging;


import com.example.loanriskevaluationsystem.loanapp.service.LoanApplicationService;
import com.example.loanriskevaluationsystem.shared.event.RiskEvaluationResultEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskEvaluationResultListener {

    private final LoanApplicationService loanService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.result}")
    public void handle(String jsonMessage) {
        log.info("Received risk evaluation result");

        try {
            RiskEvaluationResultEvent event = objectMapper.readValue(
                    jsonMessage,
                    RiskEvaluationResultEvent.class
            );

            log.info("Updating loan status for: {}", event.getLoanApplicationId());

            // Update loan status based on decision
            updateLoanStatus(event);

        } catch (Exception e) {
            log.error("Failed to deserialize result: {}", jsonMessage, e);
        }
    }

    private void updateLoanStatus(RiskEvaluationResultEvent event) {
        String loanId = event.getLoanApplicationId();


        switch (event.getDecision()) {
            case "APPROVE" -> {
                loanService.updateStatus(Long.valueOf(loanId),
                        com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.APPROVED);
                log.info("Loan approved: {}", loanId);
            }
            case "REJECT" -> {
                loanService.updateStatus(Long.valueOf(loanId),
                        com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.REJECTED);
                log.info("Loan rejected: {}", loanId);
            }
            case "MANUAL_REVIEW" -> {
                loanService.updateStatus(Long.valueOf(loanId),
                        com.example.loanriskevaluationsystem.loanapp.enums.LoanStatus.UNDER_REVIEW);
                log.info("Loan sent to manual review: {}", loanId);
            }
        }
    }
}