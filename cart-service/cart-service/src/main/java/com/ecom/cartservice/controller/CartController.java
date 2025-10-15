package com.ecom.cartservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.cartservice.constants.CartServiceConstants;
import com.ecom.cartservice.dto.AddToCartRequest;
import com.ecom.cartservice.dto.CartResponse;
import com.ecom.cartservice.dto.OrderResponse;
import com.ecom.cartservice.dto.SuccessResponse;
import com.ecom.cartservice.service.AuthenticationService;
import com.ecom.cartservice.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;
    private final AuthenticationService authenticationService;

    public CartController(CartService cartService, AuthenticationService authenticationService) {
        this.cartService = cartService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestParam Long userId, 
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_GET_CART_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is admin or the same user
        boolean isAdmin = authenticationService.isAdmin(authHeader);
        Long tokenUserId = authenticationService.getUserId(authHeader);
        
        if (!isAdmin && (tokenUserId == null || !userId.equals(tokenUserId))) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_ACCESS_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.ACCESS_DENIED_MESSAGE));
        }
        
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request, 
                                     @RequestParam Long userId,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_POST_ADD_TO_CART_REQUEST, request.getProductId(), userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (!authenticationService.isUser(authHeader)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is adding to their own cart
        Long tokenUserId = authenticationService.getUserId(authHeader);
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_ADD_TO_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_ADD_TO_OWN_CART_MESSAGE));
        }
        
        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId, 
                                         @RequestParam Long userId,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_DELETE_REMOVE_FROM_CART_REQUEST, productId, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (!authenticationService.isUser(authHeader)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is removing from their own cart
        Long tokenUserId = authenticationService.getUserId(authHeader);
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_REMOVE_FROM_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_REMOVE_FROM_OWN_CART_MESSAGE));
        }
        
        CartResponse cart = cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestParam Long userId,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_DELETE_CLEAR_CART_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (!authenticationService.isUser(authHeader)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is clearing their own cart
        Long tokenUserId = authenticationService.getUserId(authHeader);
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_CLEAR_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_CLEAR_OWN_CART_MESSAGE));
        }
        
        cartService.clearCart(userId);
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam Long userId,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_POST_CHECKOUT_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (!authenticationService.isUser(authHeader)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is checking out their own cart
        Long tokenUserId = authenticationService.getUserId(authHeader);
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_CHECKOUT_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_CHECKOUT_OWN_CART_MESSAGE));
        }
        
        OrderResponse order = cartService.checkout(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}