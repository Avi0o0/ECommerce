package com.ecom.cartservice.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.cartservice.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(String userId);

    boolean existsByUserId(String userId);

    List<Cart> findAllByUserId(String userId);

    void deleteByUserId(String userId);

    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM Cart c JOIN c.cartItems ci WHERE c.userId = :userId")
    int countTotalItemsByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(ci.priceAtAddition * ci.quantity), 0) FROM Cart c JOIN c.cartItems ci WHERE c.userId = :userId")
    BigDecimal calculateTotalPriceByUserId(@Param("userId") String userId);
}
