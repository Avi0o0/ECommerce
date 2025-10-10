package com.ecom.notificationservice.controller;

import com.ecom.notificationservice.dto.NotificationDto;
import com.ecom.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification Management", description = "APIs for managing notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @PostMapping
    @Operation(summary = "Send notification", description = "Send a general notification to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationDto.NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationDto.NotificationRequest notificationRequest) {
        logger.info("Received request to send notification to user: {}", notificationRequest.getUserId());
        
        NotificationDto.NotificationResponse notificationResponse = notificationService.sendNotification(notificationRequest);
        return new ResponseEntity<>(notificationResponse, HttpStatus.CREATED);
    }
    
    @PostMapping("/order")
    @Operation(summary = "Send order notification", description = "Send order-related notification to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationDto.NotificationResponse> sendOrderNotification(
            @Valid @RequestBody NotificationDto.OrderNotificationRequest orderNotificationRequest) {
        logger.info("Received request to send order notification to user: {}", orderNotificationRequest.getUserId());
        
        NotificationDto.NotificationResponse notificationResponse = notificationService.sendOrderNotification(orderNotificationRequest);
        return new ResponseEntity<>(notificationResponse, HttpStatus.CREATED);
    }
    
    @PostMapping("/payment")
    @Operation(summary = "Send payment notification", description = "Send payment-related notification to a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment notification sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationDto.NotificationResponse> sendPaymentNotification(
            @Valid @RequestBody NotificationDto.PaymentNotificationRequest paymentNotificationRequest) {
        logger.info("Received request to send payment notification to user: {}", paymentNotificationRequest.getUserId());
        
        NotificationDto.NotificationResponse notificationResponse = notificationService.sendPaymentNotification(paymentNotificationRequest);
        return new ResponseEntity<>(notificationResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Retrieve notification details by notification ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification found"),
        @ApiResponse(responseCode = "404", description = "Notification not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationDto.NotificationResponse> getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable Long notificationId) {
        logger.info("Received request to get notification: {}", notificationId);
        
        NotificationDto.NotificationResponse notificationResponse = notificationService.getNotificationById(notificationId);
        return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user ID", description = "Retrieve all notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getNotificationsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get notifications for user: {}", userId);
        
        List<NotificationDto.NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get paginated notifications by user ID", description = "Retrieve paginated notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<NotificationDto.NotificationResponse>> getNotificationsByUserIdPaged(
            @Parameter(description = "User ID") @PathVariable Long userId,
            Pageable pageable) {
        logger.info("Received request to get paginated notifications for user: {}", userId);
        
        Page<NotificationDto.NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieve all notifications with a specific type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid notification type"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getNotificationsByType(
            @Parameter(description = "Notification type") @PathVariable String type) {
        logger.info("Received request to get notifications by type: {}", type);
        
        List<NotificationDto.NotificationResponse> notifications = notificationService.getNotificationsByType(type);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Retrieve all notifications with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid notification status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable String status) {
        logger.info("Received request to get notifications by status: {}", status);
        
        List<NotificationDto.NotificationResponse> notifications = notificationService.getNotificationsByStatus(status);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/type/{type}")
    @Operation(summary = "Get notifications by user ID and type", description = "Retrieve notifications for a specific user and type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid notification type"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationDto.NotificationResponse>> getNotificationsByUserIdAndType(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Notification type") @PathVariable String type) {
        logger.info("Received request to get notifications for user {} by type: {}", userId, type);
        
        List<NotificationDto.NotificationResponse> notifications = notificationService.getNotificationsByUserIdAndType(userId, type);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "Get notification statistics", description = "Get notification statistics for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NotificationService.NotificationStats> getNotificationStats(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get notification statistics for user: {}", userId);
        
        NotificationService.NotificationStats stats = notificationService.getNotificationStats(userId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
    @PostMapping("/retry")
    @Operation(summary = "Retry failed notifications", description = "Retry sending failed notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Retry process started"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> retryFailedNotifications() {
        logger.info("Received request to retry failed notifications");
        
        notificationService.retryFailedNotifications();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
