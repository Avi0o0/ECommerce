package com.ecom.cartservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.cartservice.client.OrderServiceClient;
import com.ecom.cartservice.client.ProductServiceClient;
import com.ecom.cartservice.constants.CartServiceConstants;
import com.ecom.cartservice.dto.AddToCartRequest;
import com.ecom.cartservice.dto.CartResponse;
import com.ecom.cartservice.dto.OrderItemRequest;
import com.ecom.cartservice.dto.OrderRequest;
import com.ecom.cartservice.dto.OrderResponse;
import com.ecom.cartservice.dto.ProductDto;
import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;
import com.ecom.cartservice.exception.CartItemNotFoundException;
import com.ecom.cartservice.exception.CartNotFoundException;
import com.ecom.cartservice.exception.ProductNotAvailableException;
import com.ecom.cartservice.exception.ProductNotFoundException;
import com.ecom.cartservice.repository.CartItemRepository;
import com.ecom.cartservice.repository.CartRepository;

@Service
@Transactional
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final OrderServiceClient orderServiceClient;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                      ProductServiceClient productServiceClient, OrderServiceClient orderServiceClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceClient = productServiceClient;
        this.orderServiceClient = orderServiceClient;
    }

    /**
     * Get cart for user
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        logger.info(CartServiceConstants.LOG_GETTING_CART_FOR_USER, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CartServiceConstants.CART_NOT_FOUND_MESSAGE + userId));
        return convertToResponse(cart);
    }

    /**
     * Add product to cart
     */
    public CartResponse addToCart(String userId, AddToCartRequest request) {
        logger.info(CartServiceConstants.LOG_ADDING_PRODUCT_TO_CART, request.getProductId(), userId);

        // Get product details and check availability
        ProductDto.ProductResponse product = null;
        logger.debug("Calling Product Service for product ID: {}", request.getProductId());
        try {
        	product = productServiceClient.getProductById(request.getProductId());
            logger.debug("Product Service response: {}", product);
        } catch (Exception e) {
        	logger.error("Product Service returned null for product ID: {}", request.getProductId());
            throw new ProductNotFoundException(CartServiceConstants.PRODUCT_NOT_FOUND_MESSAGE);
		}
        
        if (product == null) {
            logger.error("Product Service returned null for product ID: {}", request.getProductId());
            throw new ProductNotAvailableException(CartServiceConstants.PRODUCT_NOT_FOUND_MESSAGE);
        }
        
        logger.debug("Product details - ID: {}, Name: {}, Price: {}, Stock: {}", 
            product.getId(), product.getName(), product.getPrice(), product.getStockQuantity());
        
        logger.info("@@@Product details - ID: {}, Name: {}, Price: {}, Stock: {}", 
                product.getId(), product.getName(), product.getPrice(), product.getStockQuantity());
        
        // Check if product is available
        if (!product.isAvailable()) {
            throw new ProductNotAvailableException(String.format(CartServiceConstants.PRODUCT_NOT_AVAILABLE_MESSAGE, 
                0, request.getQuantity()));
        }
        
        // Check if product has sufficient stock
        if (!product.hasSufficientStock(request.getQuantity())) {
            throw new ProductNotAvailableException(String.format(CartServiceConstants.PRODUCT_INSUFFICIENT_STOCK_MESSAGE, 
                product.getStockQuantity(), request.getQuantity()));
        }

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            logger.info(CartServiceConstants.LOG_CREATING_NEW_CART_FOR_USER, userId);
            return cartRepository.save(new Cart(userId));
        });

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Check if new quantity exceeds available stock
            if (newQuantity > product.getStockQuantity()) {
                throw new ProductNotAvailableException(String.format(CartServiceConstants.PRODUCT_INSUFFICIENT_STOCK_MESSAGE, 
                    product.getStockQuantity(), newQuantity));
            }
            
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            logger.info(CartServiceConstants.LOG_UPDATED_QUANTITY_FOR_PRODUCT, request.getProductId(), cart.getId(), item.getQuantity());
        } else {
            // Ensure we have a valid price
            BigDecimal productPrice = product.getPrice();
            if (productPrice == null) {
                logger.error("Product price is null for product ID: {}", request.getProductId());
                throw new ProductNotAvailableException("Product price information is not available");
            }
            
            logger.debug("Creating CartItem with - Cart ID: {}, Product ID: {}, Quantity: {}, Price: {}", 
                cart.getId(), request.getProductId(), request.getQuantity(), productPrice);
            
            CartItem newItem = new CartItem(cart, request.getProductId(), request.getQuantity(), productPrice);
            logger.debug("CartItem created - ID: {}, PriceAtAddition: {}", newItem.getId(), newItem.getPriceAtAddition());
            
            cart.addCartItem(newItem);
            cartItemRepository.save(newItem);
            logger.info(CartServiceConstants.LOG_ADDED_NEW_PRODUCT_TO_CART, request.getProductId(), cart.getId());
        }

        return convertToResponse(cart);
    }

    /**
     * Remove product from cart
     */
    public CartResponse removeFromCart(String userId, Long productId) {
        logger.info(CartServiceConstants.LOG_REMOVING_PRODUCT_FROM_CART, productId, userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CartServiceConstants.CART_NOT_FOUND_MESSAGE + userId));

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(String.format(CartServiceConstants.CART_ITEM_NOT_FOUND_MESSAGE, productId, userId)));

        cart.removeCartItem(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        logger.info(CartServiceConstants.LOG_REMOVED_PRODUCT_FROM_CART, productId, cart.getId());

        return convertToResponse(cart);
    }

    /**
     * Clear cart
     */
    public void clearCart(String userId) {
        logger.info(CartServiceConstants.LOG_CLEARING_CART_FOR_USER, userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CartServiceConstants.CART_NOT_FOUND_MESSAGE + userId));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
        logger.info(CartServiceConstants.LOG_CART_CLEARED_FOR_USER, userId);
    }

    /**
     * Checkout cart - create order
     */
    public OrderResponse checkout(String userId, String paymentMethod, String authorization) {
        logger.info(CartServiceConstants.LOG_PROCESSING_CHECKOUT_FOR_USER, userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(CartServiceConstants.CART_NOT_FOUND_MESSAGE + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new CartNotFoundException(CartServiceConstants.CART_EMPTY_MESSAGE + userId);
        }

        // Calculate total amount
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getPriceAtAddition().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Prepare order request
        List<OrderItemRequest> orderItems = cart.getCartItems().stream()
                .map(item -> new OrderItemRequest(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPriceAtAddition()
                ))
                .toList();

        OrderRequest orderRequest = new OrderRequest(userId, totalAmount, paymentMethod, orderItems);

        // Call Order Service to checkout with authorization header
        OrderResponse orderResponse = orderServiceClient.checkout(orderRequest, authorization);
        
        // Clear cart after successful order creation
        if(ORDER_STATUS_COMPLETED.equalsIgnoreCase(orderResponse.getOrderStatus())){
        	clearCart(userId);
        }
        
        logger.info(CartServiceConstants.LOG_CHECKOUT_COMPLETED_FOR_USER, userId, orderResponse.getId());
        return orderResponse;
    }

    /**
     * Convert Cart entity to CartResponse DTO
     */
    private CartResponse convertToResponse(Cart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(item -> {
                    // Get product details for each item
                    try {
                        ProductDto.ProductResponse product = productServiceClient.getProductById(item.getProductId());
                        return new CartResponse.CartItemResponse(
                                item.getId(),
                                item.getProductId(),
                                product.getName(),
                                product.getDescription(),
                                null, // No imageUrl in ProductResponse
                                item.getQuantity(),
                                item.getPriceAtAddition(),
                                product.getPrice(),
                                item.getTotalPrice(),
                                item.getCreatedAt()
                        );
                    } catch (Exception e) {
                        logger.warn(String.format(CartServiceConstants.PRODUCT_DETAILS_FETCH_ERROR_MESSAGE, item.getProductId()) + " - Error: {}", e.getMessage(), e);
                        return new CartResponse.CartItemResponse(
                                item.getId(),
                                item.getProductId(),
                                CartServiceConstants.PRODUCT_NAME_PREFIX + item.getProductId(),
                                CartServiceConstants.PRODUCT_DETAILS_UNAVAILABLE_MESSAGE,
                                CartServiceConstants.EMPTY_STRING,
                                item.getQuantity(),
                                item.getPriceAtAddition(),
                                item.getPriceAtAddition(),
                                item.getTotalPrice(),
                                item.getCreatedAt()
                        );
                    }
                })
                .toList();

        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                itemResponses,
                cart.getTotalItems(),
                totalPrice,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}