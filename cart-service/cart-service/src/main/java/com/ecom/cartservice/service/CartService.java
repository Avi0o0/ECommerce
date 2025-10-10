package com.ecom.cartservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.cartservice.client.ProductServiceClient;
import com.ecom.cartservice.dto.CartDto;
import com.ecom.cartservice.dto.ProductDto;
import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;
import com.ecom.cartservice.exception.CartItemNotFoundException;
import com.ecom.cartservice.exception.CartNotFoundException;
import com.ecom.cartservice.exception.ProductNotFoundException;
import com.ecom.cartservice.repository.CartItemRepository;
import com.ecom.cartservice.repository.CartRepository;

@Service
@Transactional
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    public CartService(CartRepository cartRepository, 
                      CartItemRepository cartItemRepository,
                      ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceClient = productServiceClient;
    }

    /**
     * Get or create cart for user
     */
    @Transactional(readOnly = true)
    public Cart getOrCreateCart(Long userId) {
        logger.info("Getting or creating cart for user: {}", userId);
        
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            logger.debug("Found existing cart for user: {}", userId);
            return existingCart.get();
        }

        logger.info("Creating new cart for user: {}", userId);
        Cart newCart = new Cart(userId);
        return cartRepository.save(newCart);
    }

    /**
     * Get cart for user
     */
    @Transactional(readOnly = true)
    public CartDto.CartResponse getCart(Long userId) {
        logger.info("Getting cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        List<CartDto.CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(this::convertToCartItemResponse)
                .toList();

        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto.CartResponse(
                cart.getId(),
                cart.getUserId(),
                cartItemResponses,
                cart.getTotalItems(),
                totalPrice,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    /**
     * Add product to cart
     */
    public CartDto.CartResponse addToCart(Long userId, CartDto.AddToCartRequest request) {
        logger.info("Adding product {} to cart for user: {}", request.getProductId(), userId);

        // Validate product exists and get current price
        ProductDto.ProductResponse product = productServiceClient.getProductById(request.getProductId());
        if (product == null || !product.getIsActive()) {
            throw new ProductNotFoundException("Product not found or inactive: " + request.getProductId());
        }

        // Get or create cart
        Cart cart = getOrCreateCart(userId);

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
            logger.info("Updated quantity for product {} in cart. New quantity: {}", request.getProductId(), cartItem.getQuantity());
        } else {
            // Add new item
            CartItem newItem = new CartItem(cart, request.getProductId(), request.getQuantity(), product.getPrice());
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
            logger.info("Added new product {} to cart with quantity: {}", request.getProductId(), request.getQuantity());
        }

        return getCart(userId);
    }

    /**
     * Update product quantity in cart
     */
    public CartDto.CartResponse updateQuantity(Long userId, CartDto.UpdateQuantityRequest request) {
        logger.info("Updating quantity for product {} in cart for user: {}", request.getProductId(), userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new CartItemNotFoundException("Product not found in cart: " + request.getProductId()));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        logger.info("Updated quantity for product {} to: {}", request.getProductId(), request.getQuantity());
        return getCart(userId);
    }

    /**
     * Remove product from cart
     */
    public CartDto.CartResponse removeFromCart(Long userId, Long productId) {
        logger.info("Removing product {} from cart for user: {}", productId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException("Product not found in cart: " + productId));

        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
        
        logger.info("Removed product {} from cart", productId);
        return getCart(userId);
    }

    /**
     * Clear cart
     */
    public void clearCart(Long userId) {
        logger.info("Clearing cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));

        cart.clearCart();
        cartItemRepository.deleteByCartId(cart.getId());
        
        logger.info("Cleared cart for user: {}", userId);
    }

    /**
     * Get cart items count for user
     */
    @Transactional(readOnly = true)
    public int getCartItemsCount(Long userId) {
        logger.debug("Getting cart items count for user: {}", userId);
        return cartRepository.countTotalItemsByUserId(userId);
    }

    /**
     * Get cart total price for user
     */
    @Transactional(readOnly = true)
    public BigDecimal getCartTotalPrice(Long userId) {
        logger.debug("Getting cart total price for user: {}", userId);
        return cartRepository.calculateTotalPriceByUserId(userId);
    }

    /**
     * Convert CartItem entity to CartItemResponse DTO
     */
    private CartDto.CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        try {
            // Get current product details
            ProductDto.ProductResponse product = productServiceClient.getProductById(cartItem.getProductId());
            
            return new CartDto.CartItemResponse(
                    cartItem.getId(),
                    cartItem.getProductId(),
                    product != null ? product.getName() : "Unknown Product",
                    product != null ? product.getDescription() : "",
                    product != null ? product.getImageUrl() : "",
                    cartItem.getQuantity(),
                    cartItem.getPriceAtAddition(),
                    product != null ? product.getPrice() : cartItem.getPriceAtAddition(),
                    cartItem.getTotalPrice(),
                    cartItem.getCreatedAt()
            );
        } catch (Exception e) {
            logger.warn("Failed to fetch product details for product ID: {}", cartItem.getProductId(), e);
            // Return response with basic information if product service is unavailable
            return new CartDto.CartItemResponse(
                    cartItem.getId(),
                    cartItem.getProductId(),
                    "Product Unavailable",
                    "Product details could not be fetched",
                    "",
                    cartItem.getQuantity(),
                    cartItem.getPriceAtAddition(),
                    cartItem.getPriceAtAddition(),
                    cartItem.getTotalPrice(),
                    cartItem.getCreatedAt()
            );
        }
    }
}
