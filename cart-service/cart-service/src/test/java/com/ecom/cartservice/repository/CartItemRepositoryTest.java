package com.ecom.cartservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("Cart Item Repository Tests")
class CartItemRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Should find cart item by cart ID and product ID")
    void shouldFindCartItemByCartIdAndProductId() {
        // Given
        Cart cart = new Cart();
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        entityManager.persist(item);
        entityManager.flush();

        // When
        Optional<CartItem> found = cartItemRepository.findByCartIdAndProductId(cart.getId(), 100L);

        // Then
        assertTrue(found.isPresent());
        assertEquals(100L, found.get().getProductId());
        assertEquals(cart.getId(), found.get().getCart().getId());
    }

    @Test
    @DisplayName("Should find all items by cart ID")
    void shouldFindAllItemsByCartId() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);

        CartItem item1 = new CartItem();
        item1.setCart(cart);
        item1.setProductId(100L);
        item1.setQuantity(2);
        item1.setPriceAtAddition(new BigDecimal("99.99"));
        entityManager.persist(item1);

        CartItem item2 = new CartItem();
        item2.setCart(cart);
        item2.setProductId(101L);
        item2.setQuantity(1);
        item2.setPriceAtAddition(new BigDecimal("149.99"));
        entityManager.persist(item2);
        entityManager.flush();

        // When
        List<CartItem> found = cartItemRepository.findByCartId(cart.getId());

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(item -> item.getProductId().equals(100L)));
        assertTrue(found.stream().anyMatch(item -> item.getProductId().equals(101L)));
    }

    @Test
    @DisplayName("Should delete all items by cart ID")
    void shouldDeleteAllItemsByCartId() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);

        CartItem item1 = new CartItem();
        item1.setCart(cart);
        item1.setProductId(100L);
        item1.setQuantity(2);
        item1.setPriceAtAddition(new BigDecimal("99.99"));
        entityManager.persist(item1);

        CartItem item2 = new CartItem();
        item2.setCart(cart);
        item2.setProductId(101L);
        item2.setQuantity(1);
        item2.setPriceAtAddition(new BigDecimal("149.99"));
        entityManager.persist(item2);
        entityManager.flush();

        // When
        cartItemRepository.deleteByCartId(cart.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        List<CartItem> found = cartItemRepository.findByCartId(cart.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should update cart item")
    void shouldUpdateCartItem() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        entityManager.persist(item);
        entityManager.flush();

        // When
        item.setQuantity(3);
        cartItemRepository.save(item);
        entityManager.flush();
        entityManager.clear();

        // Then
        CartItem found = cartItemRepository.findById(item.getId()).orElseThrow();
        assertEquals(3, found.getQuantity());
    }

    @Test
    @DisplayName("Should delete cart item")
    void shouldDeleteCartItem() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        entityManager.persist(item);
        entityManager.flush();

        // When
        cartItemRepository.delete(item);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CartItem> found = cartItemRepository.findById(item.getId());
        assertTrue(found.isEmpty());
    }
}