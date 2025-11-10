package com.ecom.orderservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.orderservice.constants.OrderServiceConstants;
import com.ecom.orderservice.dto.GlobalErrorResponse;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.service.AuthenticationService;
import com.ecom.orderservice.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final AuthenticationService authenticationService;

    public OrderController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Object> checkout(@Valid @RequestBody OrderRequest request, 
                                     @RequestHeader("Authorization") String authorization) {
        logger.info(OrderServiceConstants.LOG_POST_CHECKOUT_REQUEST, request.getUserId());
        
        try {
            // Validate JWT token - only USER role can checkout
            logger.info("isuser: {}", authenticationService.isUser(authorization));
            if (!authenticationService.isUser(authorization)) {
                logger.warn(OrderServiceConstants.LOG_UNAUTHORIZED_CHECKOUT_ATTEMPT, request.getUserId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(401, OrderServiceConstants.USER_ROLE_REQUIRED_MESSAGE, OrderServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
            }

            // Ensure the token belongs to the same userId provided in the request
            var tokenDetails = authenticationService.validateTokenAndGetDetails(authorization);
            String tokenUserId = tokenDetails.getUserId();
            if (tokenUserId == null || !tokenUserId.equals(request.getUserId())) {
                logger.warn("User {} attempted to checkout for user {}", tokenUserId, request.getUserId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new GlobalErrorResponse(403, OrderServiceConstants.UNAUTHORIZED_ACCESS_MESSAGE, "Users can only checkout for themselves"));
            }

            OrderResponse order = orderService.checkout(request);
            
            // Check if order is INCOMPLETE (payment service unavailable)
            if ("INCOMPLETE".equalsIgnoreCase(order.getOrderStatus().toString())) {
                logger.warn("Order created as INCOMPLETE due to payment service unavailability for order ID: {}", order.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GlobalErrorResponse(500, 
                        OrderServiceConstants.PAYMENT_SERVICE_UNAVAILABLE_MESSAGE, OrderServiceConstants.PAYMENT_SERVICE_UNAVAILABLE_MESSAGE));
            }
            
            logger.info(OrderServiceConstants.LOG_CHECKOUT_COMPLETED_SUCCESSFULLY, order.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_CHECKOUT_FAILED_FOR_USER, request.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GlobalErrorResponse(500, OrderServiceConstants.CHECKOUT_FAILED_MESSAGE + ": " + e.getMessage(), e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long orderId,
                                         @RequestHeader("Authorization") String authorization) {
        logger.info(OrderServiceConstants.LOG_GET_ORDER_BY_ID_REQUEST, orderId);
        
        try {
            // Validate JWT token - USER or ADMIN can access
            if (!authenticationService.isValidToken(authorization)) {
                logger.warn(OrderServiceConstants.LOG_UNAUTHORIZED_ACCESS_ATTEMPT_ORDER, orderId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(401, OrderServiceConstants.INVALID_TOKEN_MESSAGE, OrderServiceConstants.INVALID_TOKEN_MESSAGE));
            }
            
            OrderResponse order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_GETTING_ORDER_BY_ID, orderId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new GlobalErrorResponse(404, OrderServiceConstants.ORDER_NOT_FOUND_MESSAGE + orderId, e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getOrdersByUserId(@PathVariable String userId,
                                           @RequestHeader("Authorization") String authorization) {
        logger.info(OrderServiceConstants.LOG_GET_ORDERS_BY_USER_REQUEST, userId);
        
        try {
            // Validate JWT token - USER or ADMIN can access
            if (!authenticationService.isValidToken(authorization)) {
                logger.warn(OrderServiceConstants.LOG_UNAUTHORIZED_ACCESS_ATTEMPT_USER_ORDERS, userId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new GlobalErrorResponse(401, OrderServiceConstants.INVALID_TOKEN_MESSAGE, OrderServiceConstants.INVALID_TOKEN_MESSAGE));
            }
            
            List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_GETTING_ORDERS_FOR_USER, userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GlobalErrorResponse(500, OrderServiceConstants.UNEXPECTED_ERROR_MESSAGE + ": " + e.getMessage(), e.getMessage()));
        }
    }
}