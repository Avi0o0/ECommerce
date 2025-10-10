package com.ecom.notificationservice.service;

import com.ecom.notificationservice.dto.NotificationDto;
import com.ecom.notificationservice.entity.Notification;
import com.ecom.notificationservice.entity.NotificationStatus;
import com.ecom.notificationservice.entity.NotificationType;
import com.ecom.notificationservice.exception.*;
import com.ecom.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

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
     * Send a general notification
     */
    public NotificationDto.NotificationResponse sendNotification(NotificationDto.NotificationRequest notificationRequest) {
        logger.info("Sending notification to user: {}", notificationRequest.getUserId());
        
        try {
            NotificationType type = NotificationType.valueOf(notificationRequest.getType().toUpperCase());
            
            Notification notification = new Notification(
                notificationRequest.getUserId(),
                notificationRequest.getMessage(),
                type
            );
            
            notification = notificationRepository.save(notification);
            
            // Send notification asynchronously
            sendNotificationAsync(notification);
            
            logger.info("Notification created with ID: {}", notification.getId());
            return convertToNotificationResponse(notification);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid notification type '{}' provided: {}", notificationRequest.getType(), e.getMessage());
            throw new InvalidNotificationTypeException("Invalid notification type: " + notificationRequest.getType());
        }
    }
    
    /**
     * Send order notification
     */
    public NotificationDto.NotificationResponse sendOrderNotification(NotificationDto.OrderNotificationRequest orderNotificationRequest) {
        logger.info("Sending order notification to user: {} for order: {}", 
                   orderNotificationRequest.getUserId(), orderNotificationRequest.getOrderId());
        
        String message = generateOrderMessage(orderNotificationRequest.getOrderId(), orderNotificationRequest.getOrderStatus());
        
        NotificationDto.NotificationRequest notificationRequest = new NotificationDto.NotificationRequest(
            orderNotificationRequest.getUserId(),
            message,
            "ORDER"
        );
        
        return sendNotification(notificationRequest);
    }
    
    /**
     * Send payment notification
     */
    public NotificationDto.NotificationResponse sendPaymentNotification(NotificationDto.PaymentNotificationRequest paymentNotificationRequest) {
        logger.info("Sending payment notification to user: {} for payment: {}", 
                   paymentNotificationRequest.getUserId(), paymentNotificationRequest.getPaymentId());
        
        String message = generatePaymentMessage(paymentNotificationRequest.getPaymentId(), 
                                              paymentNotificationRequest.getPaymentStatus(), 
                                              paymentNotificationRequest.getAmount());
        
        NotificationDto.NotificationRequest notificationRequest = new NotificationDto.NotificationRequest(
            paymentNotificationRequest.getUserId(),
            message,
            "PAYMENT"
        );
        
        return sendNotification(notificationRequest);
    }
    
    /**
     * Send notification asynchronously
     */
    @Async
    public CompletableFuture<Void> sendNotificationAsync(Notification notification) {
        logger.info("Processing async notification for user: {}", notification.getUserId());
        
        try {
            // Simulate notification sending delay
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3 seconds
            
            // Simulate notification success/failure (90% success rate)
            boolean success = random.nextDouble() < 0.90;
            
            if (success) {
                notification.markAsSent();
                logger.info("Notification sent successfully to user: {}", notification.getUserId());
            } else {
                notification.markAsFailed();
                logger.warn("Notification failed to send to user: {}", notification.getUserId());
            }
            
            notificationRepository.save(notification);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            notification.markAsFailed();
            notificationRepository.save(notification);
            logger.error("Notification sending interrupted for user: {}", notification.getUserId());
        } catch (Exception e) {
            notification.markAsFailed();
            notificationRepository.save(notification);
            logger.error("Error sending notification to user {}: {}", notification.getUserId(), e.getMessage());
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Retry failed notifications
     */
    @Async
    public CompletableFuture<Void> retryFailedNotifications() {
        logger.info("Starting retry process for failed notifications");
        
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsForRetry();
        
        for (Notification notification : failedNotifications) {
            if (notification.canRetry()) {
                notification.incrementRetryCount();
                notificationRepository.save(notification);
                
                // Retry sending
                sendNotificationAsync(notification);
                logger.info("Retrying notification {} (attempt {})", notification.getId(), notification.getRetryCount());
            } else {
                logger.warn("Notification {} exceeded max retries", notification.getId());
            }
        }
        
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationDto.NotificationResponse getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        
        return convertToNotificationResponse(notification);
    }
    
    /**
     * Get notifications by user ID
     */
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
            .map(this::convertToNotificationResponse)
            .toList();
    }
    
    /**
     * Get notifications by user ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<NotificationDto.NotificationResponse> getNotificationsByUserId(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(this::convertToNotificationResponse);
    }
    
    /**
     * Get notifications by type
     */
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotificationsByType(String type) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationRepository.findByTypeOrderByCreatedAtDesc(notificationType);
            return notifications.stream()
                .map(this::convertToNotificationResponse)
                .toList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid notification type '{}' provided for filter: {}", type, e.getMessage());
            throw new InvalidNotificationTypeException("Invalid notification type: " + type);
        }
    }
    
    /**
     * Get notifications by status
     */
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotificationsByStatus(String status) {
        try {
            NotificationStatus notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
            List<Notification> notifications = notificationRepository.findByStatusOrderByCreatedAtDesc(notificationStatus);
            return notifications.stream()
                .map(this::convertToNotificationResponse)
                .toList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid notification status '{}' provided for filter: {}", status, e.getMessage());
            throw new InvalidNotificationStatusException("Invalid notification status: " + status);
        }
    }
    
    /**
     * Get notifications by user ID and type
     */
    @Transactional(readOnly = true)
    public List<NotificationDto.NotificationResponse> getNotificationsByUserIdAndType(Long userId, String type) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            List<Notification> notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, notificationType);
            return notifications.stream()
                .map(this::convertToNotificationResponse)
                .toList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid notification type '{}' provided for user {} filter: {}", type, userId, e.getMessage());
            throw new InvalidNotificationTypeException("Invalid notification type: " + type);
        }
    }
    
    /**
     * Get notification statistics
     */
    @Transactional(readOnly = true)
    public NotificationStats getNotificationStats(Long userId) {
        long totalNotifications = notificationRepository.countByUserId(userId);
        long sentNotifications = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.SENT);
        long failedNotifications = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.FAILED);
        
        return new NotificationStats(totalNotifications, sentNotifications, failedNotifications);
    }
    
    // Helper methods
    
    private String generateOrderMessage(Long orderId, String orderStatus) {
        return switch (orderStatus.toUpperCase()) {
            case "PENDING" -> String.format("Your order #%d has been placed and is pending payment.", orderId);
            case "PAID" -> String.format("Payment confirmed! Your order #%d is being processed.", orderId);
            case "SHIPPED" -> String.format("Great news! Your order #%d has been shipped and is on its way.", orderId);
            case "DELIVERED" -> String.format("Your order #%d has been delivered successfully. Thank you for shopping with us!", orderId);
            case "CANCELED" -> String.format("Your order #%d has been canceled. If you have any questions, please contact support.", orderId);
            default -> String.format("Update on your order #%d: Status changed to %s.", orderId, orderStatus);
        };
    }
    
    private String generatePaymentMessage(Long paymentId, String paymentStatus, String amount) {
        return switch (paymentStatus.toUpperCase()) {
            case "SUCCESS" -> String.format("Payment successful! Amount $%s has been processed for payment #%d.", amount, paymentId);
            case "FAILED" -> String.format("Payment failed for payment #%d. Please try again or contact support.", paymentId);
            case "PENDING" -> String.format("Payment #%d is being processed. Amount: $%s", paymentId, amount);
            default -> String.format("Update on payment #%d: Status changed to %s.", paymentId, paymentStatus);
        };
    }
    
    private NotificationDto.NotificationResponse convertToNotificationResponse(Notification notification) {
        return new NotificationDto.NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getMessage(),
            notification.getType().toString(),
            notification.getStatus().toString(),
            notification.getCreatedAt(),
            notification.getSentAt(),
            notification.getRetryCount(),
            notification.getMaxRetries()
        );
    }
    
    // Inner class for statistics
    public static class NotificationStats {
        private final long totalNotifications;
        private final long sentNotifications;
        private final long failedNotifications;
        
        public NotificationStats(long totalNotifications, long sentNotifications, long failedNotifications) {
            this.totalNotifications = totalNotifications;
            this.sentNotifications = sentNotifications;
            this.failedNotifications = failedNotifications;
        }
        
        public long getTotalNotifications() {
            return totalNotifications;
        }
        
        public long getSentNotifications() {
            return sentNotifications;
        }
        
        public long getFailedNotifications() {
            return failedNotifications;
        }
        
        public double getSuccessRate() {
            return totalNotifications > 0 ? (double) sentNotifications / totalNotifications * 100 : 0;
        }
    }
}
