package com.ecom.notificationservice.service;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.notificationservice.dto.NotificationRequest;
import com.ecom.notificationservice.dto.NotificationResponse;
import com.ecom.notificationservice.entity.Notification;
import com.ecom.notificationservice.exception.NotificationNotFoundException;
import com.ecom.notificationservice.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final Random random = new Random();
    private final NotificationRepository notificationRepository;
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    /**
     * Send a notification
     */
    public NotificationResponse sendNotification(NotificationRequest request) {
        logger.info("Sending notification to user: {}", request.getUserId());
        
        Notification notification = new Notification(
            request.getUserId(),
            request.getMessage(),
            request.getType()
        );
        
        // Simulate notification sending (90% success rate)
        String status = simulateNotificationSending();
        notification.setStatus(status);
        
        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification sent successfully with ID: {} and status: {}", savedNotification.getId(), status);
        
        return convertToResponse(savedNotification);
    }
    
    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long notificationId) {
        logger.info("Getting notification by ID: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));
        return convertToResponse(notification);
    }
    
    /**
     * Get notifications by user ID
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String userId) {
        logger.info("Getting notifications for user: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Simulate notification sending
     */
    private String simulateNotificationSending() {
        // 90% success rate for simulation
        return random.nextDouble() < 0.9 ? "SENT" : "FAILED";
    }
    
    /**
     * Convert Notification entity to NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getMessage(),
            notification.getType(),
            notification.getStatus(),
            notification.getCreatedAt()
        );
    }
}