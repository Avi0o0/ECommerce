package com.ecom.paymentservice.service;

import com.ecom.paymentservice.client.OrderServiceClient;
import com.ecom.paymentservice.dto.OrderDto;
import com.ecom.paymentservice.dto.PaymentDto;
import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import com.ecom.paymentservice.exception.*;
import com.ecom.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final Random random = new Random();
    
    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    
    public PaymentService(PaymentRepository paymentRepository, OrderServiceClient orderServiceClient) {
        this.paymentRepository = paymentRepository;
        this.orderServiceClient = orderServiceClient;
    }
    
    /**
     * Process payment for an order
     */
    public PaymentDto.PaymentResponse processPayment(PaymentDto.PaymentRequest paymentRequest) {
        logger.info("Processing payment for order: {} by user: {}", paymentRequest.getOrderId(), paymentRequest.getUserId());
        
        try {
            // 1. Validate order exists and belongs to user
            OrderDto.OrderResponse order = orderServiceClient.getOrderByIdAndUserId(
                paymentRequest.getOrderId(), paymentRequest.getUserId()).getBody();
            
            if (order == null) {
                throw new PaymentProcessingException("Order not found or does not belong to user");
            }
            
            // 2. Check if order is in PENDING status
            if (!"PENDING".equals(order.getStatus())) {
                throw new PaymentProcessingException("Order is not in PENDING status. Current status: " + order.getStatus());
            }
            
            // 3. Check if payment already exists for this order
            Optional<Payment> existingPayment = paymentRepository.findByOrderIdAndUserId(
                paymentRequest.getOrderId(), paymentRequest.getUserId());
            
            if (existingPayment.isPresent()) {
                throw new PaymentProcessingException("Payment already exists for this order");
            }
            
            // 4. Create payment record
            Payment payment = new Payment(
                paymentRequest.getOrderId(),
                paymentRequest.getUserId(),
                paymentRequest.getAmount(),
                paymentRequest.getPaymentMethod()
            );
            
            // 5. Generate transaction ID
            String transactionId = generateTransactionId();
            payment.setTransactionId(transactionId);
            
            // 6. Simulate payment processing
            PaymentStatus paymentStatus = simulatePaymentProcessing();
            payment.setPaymentStatus(paymentStatus);
            
            // 7. Save payment
            payment = paymentRepository.save(payment);
            
            // 8. Update order status based on payment result
            if (paymentStatus == PaymentStatus.SUCCESS) {
                OrderDto.OrderStatusUpdateRequest statusUpdate = new OrderDto.OrderStatusUpdateRequest("PAID");
                orderServiceClient.updateOrderStatus(paymentRequest.getOrderId(), statusUpdate);
                logger.info("Payment successful. Order {} status updated to PAID", paymentRequest.getOrderId());
            } else {
                logger.warn("Payment failed for order: {}", paymentRequest.getOrderId());
            }
            
            logger.info("Payment processed successfully. Transaction ID: {}, Status: {}", 
                       transactionId, paymentStatus);
            
            return convertToPaymentResponse(payment);
            
        } catch (Exception e) {
            logger.error("Error processing payment for order {}: {}", paymentRequest.getOrderId(), e.getMessage());
            throw new PaymentProcessingException("Failed to process payment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update payment status
     */
    public PaymentDto.PaymentResponse updatePaymentStatus(Long paymentId, String status) {
        logger.info("Updating payment {} status to: {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        try {
            PaymentStatus newStatus = PaymentStatus.valueOf(status.toUpperCase());
            PaymentStatus currentStatus = payment.getPaymentStatus();
            
            // Validate status transition
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                throw new InvalidPaymentStatusException(currentStatus.toString(), newStatus.toString());
            }
            
            payment.setPaymentStatus(newStatus);
            payment = paymentRepository.save(payment);
            
            // Update order status if payment is successful
            if (newStatus == PaymentStatus.SUCCESS) {
                OrderDto.OrderStatusUpdateRequest statusUpdate = new OrderDto.OrderStatusUpdateRequest("PAID");
                orderServiceClient.updateOrderStatus(payment.getOrderId(), statusUpdate);
                logger.info("Payment successful. Order {} status updated to PAID", payment.getOrderId());
            }
            
            logger.info("Payment {} status updated to: {}", paymentId, status);
            return convertToPaymentResponse(payment);
            
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentStatusException("Invalid status: " + status);
        }
    }
    
    /**
     * Process refund for a payment
     */
    public PaymentDto.PaymentResponse processRefund(PaymentDto.RefundRequest refundRequest) {
        logger.info("Processing refund for payment: {}", refundRequest.getPaymentId());
        
        Payment payment = paymentRepository.findById(refundRequest.getPaymentId())
            .orElseThrow(() -> new PaymentNotFoundException(refundRequest.getPaymentId()));
        
        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentProcessingException("Only successful payments can be refunded");
        }
        
        if (refundRequest.getRefundAmount().compareTo(payment.getAmount()) > 0) {
            throw new PaymentProcessingException("Refund amount cannot exceed payment amount");
        }
        
        try {
            // Simulate refund processing
            boolean refundSuccess = simulateRefundProcessing();
            
            if (refundSuccess) {
                // Create refund record (for simplicity, we'll update the existing payment)
                payment.setPaymentStatus(PaymentStatus.FAILED); // Mark as failed to indicate refund
                payment = paymentRepository.save(payment);
                
                // Update order status to CANCELED
                OrderDto.OrderStatusUpdateRequest statusUpdate = new OrderDto.OrderStatusUpdateRequest("CANCELED");
                orderServiceClient.updateOrderStatus(payment.getOrderId(), statusUpdate);
                
                logger.info("Refund processed successfully for payment: {}", refundRequest.getPaymentId());
            } else {
                throw new PaymentProcessingException("Refund processing failed");
            }
            
            return convertToPaymentResponse(payment);
            
        } catch (Exception e) {
            logger.error("Error processing refund for payment {}: {}", refundRequest.getPaymentId(), e.getMessage());
            throw new PaymentProcessingException("Failed to process refund: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public PaymentDto.PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        return convertToPaymentResponse(payment);
    }
    
    /**
     * Get payment by transaction ID
     */
    @Transactional(readOnly = true)
    public PaymentDto.PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found with transaction ID: " + transactionId));
        
        return convertToPaymentResponse(payment);
    }
    
    /**
     * Get payment by order ID and user ID (for security)
     */
    @Transactional(readOnly = true)
    public PaymentDto.PaymentResponse getPaymentByOrderIdAndUserId(Long orderId, Long userId) {
        Payment payment = paymentRepository.findByOrderIdAndUserId(orderId, userId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        
        return convertToPaymentResponse(payment);
    }
    
    /**
     * Get payment history by user ID
     */
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentHistoryByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
            .map(this::convertToPaymentResponse)
            .toList();
    }
    
    /**
     * Get payment history by user ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<PaymentDto.PaymentResponse> getPaymentHistoryByUserId(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return payments.map(this::convertToPaymentResponse);
    }
    
    /**
     * Get payments by status
     */
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByStatus(String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentRepository.findByPaymentStatusOrderByCreatedAtDesc(paymentStatus);
            return payments.stream()
                .map(this::convertToPaymentResponse)
                .toList();
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentStatusException("Invalid status: " + status);
        }
    }
    
    /**
     * Get payments by payment method
     */
    @Transactional(readOnly = true)
    public List<PaymentDto.PaymentResponse> getPaymentsByPaymentMethod(String paymentMethod) {
        List<Payment> payments = paymentRepository.findByPaymentMethodOrderByCreatedAtDesc(paymentMethod);
        return payments.stream()
            .map(this::convertToPaymentResponse)
            .toList();
    }
    
    /**
     * Calculate total amount paid by user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountPaidByUser(Long userId) {
        return paymentRepository.calculateTotalAmountPaidByUser(userId);
    }
    
    // Helper methods
    
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    private PaymentStatus simulatePaymentProcessing() {
        // Simulate payment processing with 85% success rate
        boolean success = random.nextDouble() < 0.85;
        return success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
    
    private boolean simulateRefundProcessing() {
        // Simulate refund processing with 95% success rate
        return random.nextDouble() < 0.95;
    }
    
    private boolean isValidStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == PaymentStatus.SUCCESS || newStatus == PaymentStatus.FAILED;
            case SUCCESS -> newStatus == PaymentStatus.FAILED; // For refunds
            case FAILED -> false; // Cannot change from failed
        };
    }
    
    private PaymentDto.PaymentResponse convertToPaymentResponse(Payment payment) {
        return new PaymentDto.PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentStatus().toString(),
            payment.getTransactionId(),
            "USD", // Default currency
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }
}
