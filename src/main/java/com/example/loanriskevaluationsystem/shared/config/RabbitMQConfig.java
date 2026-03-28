package com.example.loanriskevaluationsystem.shared.config;

// com.example.loanriskevaluationsystem.shared.config

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.evaluation}")
    private String evaluationQueue;

    @Value("${rabbitmq.exchange.loan}")
    private String loanExchange;

    @Value("${rabbitmq.routing-key.evaluation}")
    private String evaluationRoutingKey;

    @Bean
    public Queue evaluationQueue() {
        return QueueBuilder.durable(evaluationQueue)
                .withArgument("x-dead-letter-exchange", "loan.dlx")
                .withArgument("x-dead-letter-routing-key", "loan.failed")
                .build();
    }


    @Bean
    public Queue riskResultQueue() {
        return new Queue("risk.result.queue", true);

    }

    @Bean
    public TopicExchange loanExchange() {
        return new TopicExchange(loanExchange);
    }

    @Bean
    public Binding evaluationBinding(Queue evaluationQueue, TopicExchange loanExchange) {
        return BindingBuilder.bind(evaluationQueue)
                .to(loanExchange)
                .with(evaluationRoutingKey);
    }





    @Bean
    public SerializerMessageConverter messageConverter() {
        return new SerializerMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
