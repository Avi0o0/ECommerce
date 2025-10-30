package com.ecom.paymentservice.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.paymentservice.config.RabbitMQProducer;
import com.ecom.paymentservice.constants.PaymentServiceConstants;
import com.ecom.paymentservice.dto.NotificationRequest;
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

    private final PaymentRepository paymentRepository;
    private final RabbitMQProducer rabbitMQProducer;
    
    public PaymentService(PaymentRepository paymentRepository, RabbitMQProducer rabbitMQProducer) {
        this.paymentRepository = paymentRepository;
        this.rabbitMQProducer = rabbitMQProducer;
    }
    
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        logger.info(PaymentServiceConstants.LOG_PROCESSING_PAYMENT, paymentRequest.getOrderId(), paymentRequest.getUserId());
        
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
        logger.info(PaymentServiceConstants.LOG_PAYMENT_PROCESSED_SUCCESSFULLY, savedPayment.getId(), paymentStatus);
        
        generateNotification(savedPayment);
        return convertToResponse(savedPayment);
    }

    private void generateNotification(Payment savedPayment) {
    	NotificationRequest notificationRequest = new NotificationRequest();
    	notificationRequest.setType(savedPayment.getPaymentMethod());
    	notificationRequest.setUserId(savedPayment.getUserId());
    	notificationRequest.setMessage("This is notification regarding the transection " + savedPayment.getTransactionId() + ", your payment of Rs." + savedPayment.getAmount() + " using your " + savedPayment.getPaymentMethod() + " is " + savedPayment.getPaymentStatus());
		rabbitMQProducer.sendNotificationMessage(notificationRequest);
	}

	@Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        logger.info(PaymentServiceConstants.LOG_GETTING_PAYMENT_BY_ID, paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(PaymentServiceConstants.PAYMENT_NOT_FOUND_MESSAGE + paymentId));
        return convertToResponse(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        logger.info(PaymentServiceConstants.LOG_GETTING_PAYMENTS_FOR_USER, userId);
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return payments.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    private String generateTransactionId() {
        return PaymentServiceConstants.TRANSACTION_ID_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
 
    private String simulatePaymentProcessing() {
        return random.nextDouble() < PaymentServiceConstants.PAYMENT_SUCCESS_RATE ? 
            PaymentServiceConstants.PAYMENT_STATUS_SUCCESS : 
            PaymentServiceConstants.PAYMENT_STATUS_FAILED;
    }
    
    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getPaymentStatus().toString(),
            payment.getTransactionId(),
            PaymentServiceConstants.DEFAULT_CURRENCY,
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }
}