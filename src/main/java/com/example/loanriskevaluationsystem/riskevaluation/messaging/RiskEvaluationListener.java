package com.example.loanriskevaluationsystem.riskevaluation.messaging;


import com.example.loanriskevaluationsystem.riskevaluation.service.RiskEvaluationService;
import com.example.loanriskevaluationsystem.shared.event.LoanApplicationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RiskEvaluationListener {

    private final RiskEvaluationService service;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.evaluation}")
    public void handle(String jsonMessage) {
        log.info("Received message from queue");

        try {
            LoanApplicationEvent event = objectMapper.readValue(
                    jsonMessage,
                    LoanApplicationEvent.class
            );

            log.info("Processing loan application: {}", event.getLoanApplicationId());
            service.evaluateRisk(Long.valueOf(event.getLoanApplicationId()));

        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", jsonMessage, e);
            // TODO: Send to dead letter queue
        }
    }
}