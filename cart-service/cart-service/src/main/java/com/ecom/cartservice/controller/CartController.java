package com.ecom.cartservice.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.cartservice.dto.CartDto;
import com.ecom.cartservice.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart(@RequestParam Long userId) {
        logger.info("GET /cart - Getting cart for user: {}", userId);
        CartDto.CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto.CartResponse> addToCart(
            @Valid @RequestBody CartDto.AddToCartRequest request,
            @RequestParam Long userId) {
        logger.info("POST /cart/add - Adding product {} to cart for user: {}", request.getProductId(), userId);
        CartDto.CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<CartDto.CartResponse> updateQuantity(
            @Valid @RequestBody CartDto.UpdateQuantityRequest request,
            @RequestParam Long userId) {
        logger.info("PUT /cart/update - Updating quantity for product {} to {} for user: {}", 
                   request.getProductId(), request.getQuantity(), userId);
        CartDto.CartResponse cart = cartService.updateQuantity(userId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto.CartResponse> removeFromCart(
            @PathVariable Long productId,
            @RequestParam Long userId) {
        logger.info("DELETE /cart/remove/{} - Removing product from cart for user: {}", productId, userId);
        CartDto.CartResponse cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        logger.info("DELETE /cart/clear - Clearing cart for user: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemsCount(@RequestParam Long userId) {
        logger.info("GET /cart/count - Getting cart items count for user: {}", userId);
        int count = cartService.getCartItemsCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getCartTotalPrice(@RequestParam Long userId) {
        logger.info("GET /cart/total - Getting cart total price for user: {}", userId);
        BigDecimal total = cartService.getCartTotalPrice(userId);
        return ResponseEntity.ok(total);
    }
}
