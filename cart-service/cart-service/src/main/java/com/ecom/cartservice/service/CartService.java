package com.ecom.cartservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.cartservice.dto.AddToCartRequest;
import com.ecom.cartservice.dto.CartResponse;
import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;
import com.ecom.cartservice.exception.CartItemNotFoundException;
import com.ecom.cartservice.exception.CartNotFoundException;
import com.ecom.cartservice.repository.CartItemRepository;
import com.ecom.cartservice.repository.CartRepository;

@Service
@Transactional
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private static final String CART_NOT_FOUND_MESSAGE = "Cart not found for user: ";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Get cart for user
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        logger.info("Getting cart for user: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND_MESSAGE + userId));
        return convertToResponse(cart);
    }

    /**
     * Add product to cart
     */
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        logger.info("Adding product {} to cart for user: {}", request.getProductId(), userId);

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            logger.info("Creating new cart for user: {}", userId);
            return cartRepository.save(new Cart(userId));
        });

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            logger.info("Updated quantity for product {} in cart {} to {}", request.getProductId(), cart.getId(), item.getQuantity());
        } else {
            CartItem newItem = new CartItem(cart, request.getProductId(), request.getQuantity());
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
            logger.info("Added new product {} to cart {}", request.getProductId(), cart.getId());
        }

        return convertToResponse(cart);
    }

    /**
     * Remove product from cart
     */
    public CartResponse removeFromCart(Long userId, Long productId) {
        logger.info("Removing product {} from cart for user: {}", productId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND_MESSAGE + userId));

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Product " + productId + " not found in cart for user: " + userId));

        cart.removeCartItem(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        logger.info("Removed product {} from cart {}", productId, cart.getId());

        return convertToResponse(cart);
    }

    /**
     * Clear cart
     */
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND_MESSAGE + userId));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        logger.info("Cart cleared for user: {}", userId);
    }

    /**
     * Convert Cart entity to CartResponse DTO
     */
    private CartResponse convertToResponse(Cart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(item -> new CartResponse.CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getCreatedAt(),
                itemResponses
        );
    }
}