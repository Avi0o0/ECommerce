package com.ecom.cartservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.cartservice.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find cart item by cart ID and product ID
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Find all cart items for a specific cart
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Find all cart items for a specific product across all carts
     */
    List<CartItem> findByProductId(Long productId);

    /**
     * Check if cart item exists for cart and product
     */
    boolean existsByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete cart item by cart ID and product ID
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete all cart items for a specific cart
     */
    void deleteByCartId(Long cartId);

    /**
     * Find cart items by user ID (through cart)
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    /**
     * Find cart item by user ID and product ID
     */
    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE c.userId = :userId AND ci.productId = :productId")
    Optional<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * Count cart items for a specific cart
     */
    long countByCartId(Long cartId);
}
