package com.ecom.orderservice.controller;

import com.ecom.orderservice.dto.OrderDto;
import com.ecom.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    @Operation(summary = "Place a new order", description = "Create a new order with specified items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> placeOrder(
            @Valid @RequestBody OrderDto.OrderRequest orderRequest) {
        logger.info("Received request to place order for user: {}", orderRequest.getUserId());
        
        OrderDto.OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
    
    @PostMapping("/from-cart")
    @Operation(summary = "Place order from cart", description = "Create a new order from user's cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Cart is empty or invalid"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> placeOrderFromCart(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        logger.info("Received request to place order from cart for user: {}", userId);
        
        OrderDto.OrderResponse orderResponse = orderService.placeOrderFromCart(userId);
        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        logger.info("Received request to get order: {}", orderId);
        
        OrderDto.OrderResponse orderResponse = orderService.getOrderById(orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
    
    @GetMapping("/{orderId}/user/{userId}")
    @Operation(summary = "Get order by ID and user ID", description = "Retrieve order details by order ID and user ID for security")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> getOrderByIdAndUserId(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get order {} for user: {}", orderId, userId);
        
        OrderDto.OrderResponse orderResponse = orderService.getOrderByIdAndUserId(orderId, userId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get order history by user ID", description = "Retrieve all orders for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrderHistoryByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get order history for user: {}", userId);
        
        List<OrderDto.OrderResponse> orders = orderService.getOrderHistoryByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get paginated order history by user ID", description = "Retrieve paginated orders for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<OrderDto.OrderResponse>> getOrderHistoryByUserIdPaged(
            @Parameter(description = "User ID") @PathVariable Long userId,
            Pageable pageable) {
        logger.info("Received request to get paginated order history for user: {}", userId);
        
        Page<OrderDto.OrderResponse> orders = orderService.getOrderHistoryByUserId(userId, pageable);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve all orders with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Order status") @PathVariable String status) {
        logger.info("Received request to get orders by status: {}", status);
        
        List<OrderDto.OrderResponse> orders = orderService.getOrdersByStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or status transition"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Valid @RequestBody OrderDto.OrderStatusUpdateRequest statusUpdateRequest) {
        logger.info("Received request to update order {} status to: {}", orderId, statusUpdateRequest.getStatus());
        
        OrderDto.OrderResponse orderResponse = orderService.updateOrderStatus(orderId, statusUpdateRequest.getStatus());
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
    
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an existing order and restore stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order canceled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be canceled"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        logger.info("Received request to cancel order: {}", orderId);
        
        OrderDto.OrderResponse orderResponse = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
    
    @PostMapping("/{orderId}/payment")
    @Operation(summary = "Process payment for order", description = "Process payment for a pending order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be paid"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderDto.OrderResponse> processPayment(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "Payment method") @RequestParam String paymentMethod) {
        logger.info("Received request to process payment for order: {}", orderId);
        
        OrderDto.OrderResponse orderResponse = orderService.processPayment(orderId, paymentMethod);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
}
