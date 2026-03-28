package com.example.loanriskevaluationsystem.riskevaluation.messaging;

import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import com.example.loanriskevaluationsystem.shared.event.RiskEvaluationResultEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskEvaluationResultPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.loan}")
    private String exchange;

    @Value("${rabbitmq.routing-key.result}")
    private String resultRoutingKey;

    public void publish(RiskEvaluation evaluation) {
        try {
            RiskEvaluationResultEvent event = buildEvent(evaluation);
            String json = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(exchange, resultRoutingKey, json);
            log.info("Published risk evaluation result: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Failed to serialize result event", e);
            throw new RuntimeException("Result serialization failed", e);
        }
    }

    private RiskEvaluationResultEvent buildEvent(RiskEvaluation evaluation) {
        return RiskEvaluationResultEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .loanApplicationId(String.valueOf(evaluation.getLoanApplication().getId()))
                .riskEvaluationId(String.valueOf(evaluation.getId()))
                .riskScore(evaluation.getRiskScore())
                .riskLevel(evaluation.getRiskLevel().name())
                .decision(evaluation.getDecision().name())
                .maxApprovedAmount(evaluation.getMaxApprovedAmount())
                .suggestedInterestRate(evaluation.getSuggestedInterestRate())
                .evaluatedAt(LocalDateTime.now())
                .build();
    }
}