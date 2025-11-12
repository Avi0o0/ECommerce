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
import com.ecom.cartservice.dto.GlobalErrorResponse;
import com.ecom.cartservice.dto.OrderResponse;
import com.ecom.cartservice.dto.SuccessResponse;
import com.ecom.cartservice.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private static final String ISUSER = "isUser";
    private static final String ISADMIN = "isAdmin";
    private static final String USERID = "userId";
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Object> getCart(HttpServletRequest request, @RequestParam String userId, 
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_GET_CART_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE, CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if(Boolean.TRUE.equals(request.getAttribute(ISADMIN))) {
        	logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_ACCESS_CART, "ADMIN", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.ACCESS_DENIED_MESSAGE));
        }
        
        CartResponse cart = cartService.getCart(userId, authHeader);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addToCart(HttpServletRequest servletRequest, @Valid @RequestBody AddToCartRequest request, 
                                     @RequestParam String userId,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_POST_ADD_TO_CART_REQUEST, request.getProductId(), userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE, CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (Boolean.FALSE.equals(servletRequest.getAttribute(ISUSER))) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE, CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is adding to their own cart
        String tokenUserId = servletRequest.getAttribute(USERID).toString();
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_ADD_TO_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_ADD_TO_OWN_CART_MESSAGE));
        }
        
        CartResponse cart = cartService.addToCart(userId, request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Object> removeFromCart(HttpServletRequest servletRequest, @PathVariable Long productId, 
                                         @RequestParam String userId,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_DELETE_REMOVE_FROM_CART_REQUEST, productId, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE, CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (Boolean.FALSE.equals(servletRequest.getAttribute(ISUSER))) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE, CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is removing from their own cart
        String tokenUserId = servletRequest.getAttribute(USERID).toString();
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_REMOVE_FROM_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_REMOVE_FROM_OWN_CART_MESSAGE, CartServiceConstants.CAN_ONLY_REMOVE_FROM_OWN_CART_MESSAGE));
        }
        
        CartResponse cart = cartService.removeFromCart(userId, productId, authHeader);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Object> clearCart(HttpServletRequest servletRequest, @RequestParam String userId,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_DELETE_CLEAR_CART_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE, CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (Boolean.FALSE.equals(servletRequest.getAttribute(ISUSER))) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE, CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is clearing their own cart
        String tokenUserId = servletRequest.getAttribute(USERID).toString();
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_CLEAR_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_CLEAR_OWN_CART_MESSAGE, CartServiceConstants.CAN_ONLY_CLEAR_OWN_CART_MESSAGE));
        }
        
        cartService.clearCart(userId);
        CartResponse cart = cartService.getCart(userId, authHeader);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Object> checkout(HttpServletRequest servletRequest, @RequestParam String userId,
                                    @RequestParam String paymentMethod,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(CartServiceConstants.LOG_POST_CHECKOUT_REQUEST, userId);
        
        if (authHeader == null) {
            logger.warn(CartServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(), CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE, CartServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        // Check if user is authenticated and has USER role
        if (Boolean.FALSE.equals(servletRequest.getAttribute(ISUSER))) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE, CartServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
        }
        
        // Check if user is checking out their own cart
        String tokenUserId = servletRequest.getAttribute(USERID).toString();
        if (tokenUserId == null || !userId.equals(tokenUserId)) {
            logger.warn(CartServiceConstants.LOG_ACCESS_DENIED_USER_CANNOT_CHECKOUT_CART, tokenUserId, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(), CartServiceConstants.CAN_ONLY_CHECKOUT_OWN_CART_MESSAGE, CartServiceConstants.CAN_ONLY_CHECKOUT_OWN_CART_MESSAGE));
        }
        
        OrderResponse order = cartService.checkout(userId, paymentMethod, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}