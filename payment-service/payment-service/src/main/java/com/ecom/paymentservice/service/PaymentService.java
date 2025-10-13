package com.ecom.paymentservice.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import com.ecom.paymentservice.exception.PaymentNotFoundException;
import com.ecom.paymentservice.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final Random random = new Random();
    private static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment not found with ID: ";

    private final PaymentRepository paymentRepository;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    /**
     * Process payment for an order
     */
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        logger.info("Processing payment for order: {} by user: {}", paymentRequest.getOrderId(), paymentRequest.getUserId());
        
        Payment payment = new Payment(
            paymentRequest.getOrderId(),
            paymentRequest.getUserId(),
            paymentRequest.getAmount(),
            paymentRequest.getPaymentMethod()
        );
        
        String transactionId = generateTransactionId();
        payment.setTransactionId(transactionId);
        
        String paymentStatus = simulatePaymentProcessing();
        payment.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
        
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment processed successfully with ID: {} and status: {}", savedPayment.getId(), paymentStatus);
        
        return convertToResponse(savedPayment);
    }
    
    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        logger.info("Getting payment by ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND_MESSAGE + paymentId));
        return convertToResponse(payment);
    }
    
    /**
     * Get payments by user ID
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        logger.info("Getting payments for user: {}", userId);
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    /**
     * Simulate payment processing
     */
    private String simulatePaymentProcessing() {
        // 80% success rate for simulation
        return random.nextDouble() < 0.8 ? "SUCCESS" : "FAILED";
    }
    
    /**
     * Convert Payment entity to PaymentResponse DTO
     */
    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentStatus().toString(),
            payment.getTransactionId(),
            payment.getCreatedAt()
        );
    }
}