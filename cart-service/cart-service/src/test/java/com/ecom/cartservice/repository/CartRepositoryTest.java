package com.ecom.cartservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;

@DataJpaTest
@DisplayName("Cart Repository Tests")
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    @DisplayName("Should find cart by user ID")
    void shouldFindCartByUserId() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());
        entityManager.persist(cart);
        entityManager.flush();

        // When
        Optional<Cart> found = cartRepository.findByUserId("1L");

        // Then
        assertTrue(found.isPresent());
        assertEquals("1L", found.get().getUserId());
    }

    @Test
    @DisplayName("Should return empty when cart not found")
    void shouldReturnEmpty_whenCartNotFound() {
        // When
        Optional<Cart> found = cartRepository.findByUserId("999L");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should save cart with items")
    void shouldSaveCartWithItems() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());

        CartItem item = new CartItem();
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        item.setCart(cart);
        cart.getCartItems().add(item);

        // When
        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();
        entityManager.clear();

        // Then
        Cart found = cartRepository.findById(savedCart.getId()).orElseThrow();
        assertNotNull(found);
        assertEquals(1, found.getCartItems().size());
        CartItem savedItem = found.getCartItems().iterator().next();
        assertEquals(100L, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(0, new BigDecimal("99.99").compareTo(savedItem.getPriceAtAddition()));
    }

    @Test
    @DisplayName("Should delete cart and items")
    void shouldDeleteCartAndItems() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());

        CartItem item = new CartItem();
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        item.setCart(cart);
        cart.getCartItems().add(item);

        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();

        // When
        cartRepository.deleteById(savedCart.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Cart> found = cartRepository.findById(savedCart.getId());
        assertTrue(found.isEmpty());

        // Verify cascade delete
        Optional<CartItem> foundItem = cartItemRepository.findById(item.getId());
        assertTrue(foundItem.isEmpty());
    }

    @Test
    @DisplayName("Should update cart items")
    void shouldUpdateCartItems() {
        // Given
        Cart cart = new Cart();
        cart.setUserId("1L");
        cart.setCreatedAt(LocalDateTime.now());

        CartItem item = new CartItem();
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPriceAtAddition(new BigDecimal("99.99"));
        item.setCart(cart);
        cart.getCartItems().add(item);

        Cart savedCart = cartRepository.save(cart);
        entityManager.flush();
        entityManager.clear();

        // When
        Cart toUpdate = cartRepository.findById(savedCart.getId()).orElseThrow();
        CartItem itemToUpdate = toUpdate.getCartItems().iterator().next();
        itemToUpdate.setQuantity(3);
        cartRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // Then
        Cart updated = cartRepository.findById(savedCart.getId()).orElseThrow();
        assertEquals(1, updated.getCartItems().size());
        CartItem updatedItem = updated.getCartItems().iterator().next();
        assertEquals(3, updatedItem.getQuantity());
    }
}