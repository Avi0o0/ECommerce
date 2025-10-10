package com.ecom.cartservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.cartservice.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find cart by user ID
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Check if cart exists for user
     */
    boolean existsByUserId(Long userId);

    /**
     * Find all carts for a specific user (in case of multiple carts per user in future)
     */
    List<Cart> findAllByUserId(Long userId);

    /**
     * Delete cart by user ID
     */
    void deleteByUserId(Long userId);

    /**
     * Count total items in cart for a user
     */
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM Cart c JOIN c.cartItems ci WHERE c.userId = :userId")
    int countTotalItemsByUserId(@Param("userId") Long userId);

    /**
     * Calculate total price of cart for a user
     */
    @Query("SELECT COALESCE(SUM(ci.priceAtAddition * ci.quantity), 0) FROM Cart c JOIN c.cartItems ci WHERE c.userId = :userId")
    java.math.BigDecimal calculateTotalPriceByUserId(@Param("userId") Long userId);
}
