package com.ecom.orderservice.repository;

import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders by user ID
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find all orders by user ID with pagination
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
    /**
     * Find orders by user ID and status
     */
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
    
    /**
     * Find orders created between two dates
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by user ID created between two dates
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByUserBetweenDates(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count orders by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Find pending orders older than specified time
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Find order by ID and user ID (for security)
     */
    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
