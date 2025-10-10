package com.ecom.orderservice.repository;

import com.ecom.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find all order items by order ID
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Find order items by product ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    /**
     * Find order items by order ID and product ID
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.productId = :productId")
    List<OrderItem> findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
    
    /**
     * Count order items by product ID
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.productId = :productId")
    long countByProductId(@Param("productId") Long productId);
    
    /**
     * Find order items with quantity greater than specified value
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity > :quantity")
    List<OrderItem> findByQuantityGreaterThan(@Param("quantity") Integer quantity);
    
    /**
     * Delete order items by order ID
     */
    void deleteByOrderId(Long orderId);
}
