package com.example.loanriskevaluationsystem.loanapp.messaging;

import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import com.example.loanriskevaluationsystem.shared.event.LoanApplicationEvent;
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
public class LoanApplicationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.exchange.loan}")
    private String exchange;

    @Value("${rabbitmq.routing-key.evaluation}")
    private String routingKey;

    public void publish(LoanApplication loan) {
        try {
            LoanApplicationEvent event = buildEvent(loan);
            String json = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(exchange, routingKey, json);
            log.info("Published loan application event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Failed to serialize loan application event", e);
            throw new RuntimeException("Event serialization failed", e);
        }
    }

    private LoanApplicationEvent buildEvent(LoanApplication loan) {
        return LoanApplicationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .loanApplicationId(String.valueOf(loan.getId()))
                .customerId(loan.getCustomerId())
                .requestedAmount(loan.getRequestedAmount())
                .creditScore(loan.getCreditScore())
                .monthlyIncome(loan.getMonthlyIncome())
                .existingDebt(loan.getExistingDebtAmount())
                .timestamp(LocalDateTime.now())
                .eventType(LoanApplicationEvent.EventType.RISK_EVALUATION_REQUESTED)
                .build();
    }
}
