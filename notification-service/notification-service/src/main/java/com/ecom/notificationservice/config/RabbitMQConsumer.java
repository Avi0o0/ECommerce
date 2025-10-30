package com.ecom.notificationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ecom.notificationservice.dto.NotificationRequest;
import com.ecom.notificationservice.service.NotificationService;

@Component
public class RabbitMQConsumer {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
	private final NotificationService notificationService;

	public RabbitMQConsumer(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
	public void receiveMessage(NotificationRequest message) {
		logger.info("Received notification detail: {}", message);
		notificationService.sendNotification(message);
	}
}
