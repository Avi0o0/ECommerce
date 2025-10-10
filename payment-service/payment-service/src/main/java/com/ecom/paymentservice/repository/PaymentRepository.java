package com.ecom.paymentservice.repository;

import com.ecom.paymentservice.entity.Payment;
import com.ecom.paymentservice.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payments by order ID
     */
    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    /**
     * Find payments by user ID
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find payments by user ID with pagination
     */
    Page<Payment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find payments by status
     */
    List<Payment> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);
    
    /**
     * Find payments by user ID and status
     */
    List<Payment> findByUserIdAndPaymentStatusOrderByCreatedAtDesc(Long userId, PaymentStatus paymentStatus);
    
    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payments by order ID and user ID (for security)
     */
    Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);
    
    /**
     * Find payments created between two dates
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find payments by user ID created between two dates
     */
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByUserBetweenDates(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count payments by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count payments by status
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Find payments by payment method
     */
    List<Payment> findByPaymentMethodOrderByCreatedAtDesc(String paymentMethod);
    
    /**
     * Find payments with amount greater than specified value
     */
    @Query("SELECT p FROM Payment p WHERE p.amount > :amount ORDER BY p.createdAt DESC")
    List<Payment> findByAmountGreaterThan(@Param("amount") BigDecimal amount);
    
    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Calculate total amount paid by user
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.userId = :userId AND p.paymentStatus = 'SUCCESS'")
    BigDecimal calculateTotalAmountPaidByUser(@Param("userId") Long userId);
}
