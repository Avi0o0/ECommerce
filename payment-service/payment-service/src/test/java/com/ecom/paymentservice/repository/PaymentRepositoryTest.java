package com.ecom.paymentservice.repository;

import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // Create test payment
        testPayment = new Payment(1L, 1L, new BigDecimal("100.00"), "CREDIT_CARD");
        testPayment.setPaymentStatus(PaymentStatus.SUCCESS);
        testPayment.setTransactionId("TX123");
        testPayment = paymentRepository.save(testPayment);
    }

    @Test
    void findByOrderIdOrderByCreatedAtDesc_ExistingOrder_ReturnsPayment() {
        List<Payment> found = paymentRepository.findByOrderIdOrderByCreatedAtDesc(1L);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getOrderId()).isEqualTo(1L);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ExistingUser_ReturnsPayment() {
        List<Payment> found = paymentRepository.findByUserIdOrderByCreatedAtDesc(1L);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_WithPagination_ReturnsPageOfPayments() {
        Page<Payment> found = paymentRepository.findByUserIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 10));
        assertThat(found).isNotEmpty();
        assertThat(found.getContent().get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void findByPaymentStatusOrderByCreatedAtDesc_SuccessStatus_ReturnsPayment() {
        List<Payment> found = paymentRepository.findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus.SUCCESS);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void findByUserIdAndPaymentStatusOrderByCreatedAtDesc_ExistingUserAndStatus_ReturnsPayment() {
        List<Payment> found = paymentRepository.findByUserIdAndPaymentStatusOrderByCreatedAtDesc(1L, PaymentStatus.SUCCESS);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUserId()).isEqualTo(1L);
        assertThat(found.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void findByTransactionId_ExistingTransaction_ReturnsPayment() {
        Optional<Payment> found = paymentRepository.findByTransactionId("TX123");
        assertThat(found).isPresent();
        assertThat(found.get().getTransactionId()).isEqualTo("TX123");
    }

    @Test
    void findByOrderIdAndUserId_ExistingOrderAndUser_ReturnsPayment() {
        Optional<Payment> found = paymentRepository.findByOrderIdAndUserId(1L, 1L);
        assertThat(found).isPresent();
        assertThat(found.get().getOrderId()).isEqualTo(1L);
        assertThat(found.get().getUserId()).isEqualTo(1L);
    }

    @Test
    void findPaymentsBetweenDates_WithinRange_ReturnsPayments() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Payment> found = paymentRepository.findPaymentsBetweenDates(start, end);
        assertThat(found).isNotEmpty();
    }

    @Test
    void findPaymentsByUserBetweenDates_WithinRange_ReturnsPayments() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Payment> found = paymentRepository.findPaymentsByUserBetweenDates(1L, start, end);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void countByUserId_ExistingUser_ReturnsCount() {
        long count = paymentRepository.countByUserId(1L);
        assertThat(count).isPositive();
    }

    @Test
    void countByPaymentStatus_SuccessStatus_ReturnsCount() {
        long count = paymentRepository.countByPaymentStatus(PaymentStatus.SUCCESS);
        assertThat(count).isPositive();
    }

    @Test
    void findByPaymentMethodOrderByCreatedAtDesc_ExistingMethod_ReturnsPayments() {
        List<Payment> found = paymentRepository.findByPaymentMethodOrderByCreatedAtDesc("CREDIT_CARD");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getPaymentMethod()).isEqualTo("CREDIT_CARD");
    }

    @Test
    void findByAmountGreaterThan_ValidAmount_ReturnsPayments() {
        List<Payment> found = paymentRepository.findByAmountGreaterThan(new BigDecimal("50.00"));
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getAmount()).isGreaterThan(new BigDecimal("50.00"));
    }

    @Test
    void calculateTotalAmountPaidByUser_ExistingUser_ReturnsTotal() {
        BigDecimal total = paymentRepository.calculateTotalAmountPaidByUser(1L);
        assertThat(total).isGreaterThan(BigDecimal.ZERO);
    }
}