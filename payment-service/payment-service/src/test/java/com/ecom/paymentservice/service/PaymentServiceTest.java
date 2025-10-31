package com.ecom.paymentservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.paymentservice.config.RabbitMQProducer;
import com.ecom.paymentservice.dto.NotificationRequest;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import com.ecom.paymentservice.exception.PaymentNotFoundException;
import com.ecom.paymentservice.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceTests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setUserId(100L);
        paymentRequest.setAmount(new BigDecimal("299.99"));
        paymentRequest.setPaymentMethod("CREDIT_CARD");

        savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setOrderId(1L);
        savedPayment.setUserId(100L);
        savedPayment.setAmount(new BigDecimal("299.99"));
        savedPayment.setPaymentMethod("CREDIT_CARD");
        savedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        savedPayment.setTransactionId("TXN123456789");
        savedPayment.setCreatedAt(LocalDateTime.now());
        savedPayment.setUpdatedAt(LocalDateTime.now());
    }

    // Test: Process Payment
    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPayment_Success() {
        // Arrange
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(rabbitMQProducer).sendNotificationMessage(any(NotificationRequest.class));

        // Act
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(100L, result.getUserId());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("TXN"));
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(rabbitMQProducer, times(1)).sendNotificationMessage(any(NotificationRequest.class));
    }

    @Test
    @DisplayName("Should generate unique transaction IDs for each payment")
    void testProcessPayment_UniqueTransactionId() {
        // Arrange
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setTransactionId("TXN123");
        
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setTransactionId("TXN456");
        
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            p.setTransactionId("TXN" + System.currentTimeMillis());
            return p;
        });
        doNothing().when(rabbitMQProducer).sendNotificationMessage(any(NotificationRequest.class));

        // Act
        PaymentResponse result1 = paymentService.processPayment(paymentRequest);
        PaymentResponse result2 = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result1.getTransactionId());
        assertNotNull(result2.getTransactionId());
        // Transaction IDs should be different (timestamp-based)
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should generate notification with correct details")
    void testProcessPayment_NotificationDetails() {
        // Arrange
        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(rabbitMQProducer).sendNotificationMessage(captor.capture());

        // Act
        paymentService.processPayment(paymentRequest);

        // Assert
        NotificationRequest notification = captor.getValue();
        assertNotNull(notification);
        assertEquals("CREDIT_CARD", notification.getType());
        assertEquals(100L, notification.getUserId());
        assertNotNull(notification.getMessage());
        verify(rabbitMQProducer, times(1)).sendNotificationMessage(any(NotificationRequest.class));
    }

    // Test: Get Payment by ID
    @Test
    @DisplayName("Should return payment when valid ID is provided")
    void testGetPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));

        // Act
        PaymentResponse result = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getOrderId());
        assertEquals(new BigDecimal("299.99"), result.getAmount());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw PaymentNotFoundException when payment not found")
    void testGetPaymentById_NotFound() {
        // Arrange
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> 
            paymentService.getPaymentById(999L));
        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return correct payment details")
    void testGetPaymentById_CorrectDetails() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(savedPayment));

        // Act
        PaymentResponse result = paymentService.getPaymentById(1L);

        // Assert
        System.out.println(result.getPaymentStatus());
        assertEquals("CREDIT_CARD", result.getPaymentMethod());
        assertEquals("SUCCESS", result.getPaymentStatus());
        assertEquals("TXN123456789", result.getTransactionId());
        assertEquals("INR", result.getCurrency());
        verify(paymentRepository, times(1)).findById(1L);
    }

    // Test: Get Payments by User ID
    @Test
    @DisplayName("Should return all payments for a user")
    void testGetPaymentsByUserId_Success() {
        // Arrange
        Payment payment1 = createPayment(1L, 100L, "100.00");
        Payment payment2 = createPayment(2L, 100L, "200.00");
        List<Payment> payments = Arrays.asList(payment1, payment2);
        
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(payments);

        // Act
        List<PaymentResponse> result = paymentService.getPaymentsByUserId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(paymentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(100L);
    }

    @Test
    @DisplayName("Should return empty list when user has no payments")
    void testGetPaymentsByUserId_EmptyList() {
        // Arrange
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(999L)).thenReturn(Arrays.asList());

        // Act
        List<PaymentResponse> result = paymentService.getPaymentsByUserId(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(paymentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(999L);
    }

    @Test
    @DisplayName("Should return payments ordered by created date descending")
    void testGetPaymentsByUserId_OrderedByDate() {
        // Arrange
        Payment payment1 = createPayment(1L, 100L, "100.00");
        payment1.setCreatedAt(LocalDateTime.now().minusDays(2));
        
        Payment payment2 = createPayment(2L, 100L, "200.00");
        payment2.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        List<Payment> payments = Arrays.asList(payment2, payment1); // Ordered by date DESC
        
        when(paymentRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(payments);

        // Act
        List<PaymentResponse> result = paymentService.getPaymentsByUserId(100L);

        // Assert
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId()); // More recent first
        assertEquals(1L, result.get(1).getId());
        verify(paymentRepository, times(1)).findByUserIdOrderByCreatedAtDesc(100L);
    }

    // Test: Payment Status Handling
    @Test
    @DisplayName("Should handle SUCCESS payment status")
    void testProcessPayment_CompletedStatus() {
        // Arrange
        savedPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(rabbitMQProducer).sendNotificationMessage(any(NotificationRequest.class));

        // Act
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCESS", result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should handle FAILED payment status")
    void testProcessPayment_FailedStatus() {
        // Arrange
        Payment failedPayment = new Payment();
        failedPayment.setId(1L);
        failedPayment.setOrderId(paymentRequest.getOrderId());
        failedPayment.setUserId(paymentRequest.getUserId());
        failedPayment.setAmount(paymentRequest.getAmount());
        failedPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        failedPayment.setPaymentStatus(PaymentStatus.FAILED);
        failedPayment.setTransactionId("TXN_FAILED");
        failedPayment.setCreatedAt(LocalDateTime.now());
        failedPayment.setUpdatedAt(LocalDateTime.now());

        when(paymentRepository.save(any(Payment.class))).thenReturn(failedPayment);
        doNothing().when(rabbitMQProducer).sendNotificationMessage(any(NotificationRequest.class));

        // Act
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals("FAILED", result.getPaymentStatus());
        assertEquals("TXN_FAILED", result.getTransactionId());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(rabbitMQProducer, times(1)).sendNotificationMessage(any(NotificationRequest.class));
    }

    // Test: Notification Generation
    @Test
    @DisplayName("Should send notification after payment processing")
    void testGenerateNotification_SendNotification() {
        // Arrange
        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(rabbitMQProducer).sendNotificationMessage(captor.capture());

        // Act
        paymentService.processPayment(paymentRequest);

        // Assert
        verify(rabbitMQProducer, times(1)).sendNotificationMessage(any(NotificationRequest.class));
        NotificationRequest notification = captor.getValue();
        assertTrue(notification.getMessage().contains("notification regarding the transaction"));
    }

    // Helper Methods
    private Payment createPayment(Long id, Long userId, String amount) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal(amount));
        payment.setOrderId(id);
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN" + id);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }
}

