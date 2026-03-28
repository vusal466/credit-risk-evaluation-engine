package com.example.loanriskevaluationsystem.shared.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {

    @Bean
    public Queue testQueue() {
        return new Queue("test-queue", true);
    }

    @Bean
    public DirectExchange riskExchange() {
        return new DirectExchange("risk.exchange");
    }

    @Bean
    public Queue riskQueue() {
        return new Queue("risk.result.queue", true);
    }

    @Bean
    public Binding binding(Queue riskQueue, DirectExchange riskExchange) {
        return BindingBuilder
                .bind(riskQueue)
                .to(riskExchange)
                .with("risk.result");
    }

}
