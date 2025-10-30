package com.ecom.paymentservice.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ecom.paymentservice.dto.NotificationRequest;

@Component
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
    	this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotificationMessage(NotificationRequest notificationRequest) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, notificationRequest);
    }
}

