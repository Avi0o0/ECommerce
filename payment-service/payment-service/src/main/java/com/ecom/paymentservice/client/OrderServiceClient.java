package com.ecom.paymentservice.client;

import com.ecom.paymentservice.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {
    
    /**
     * Update order status
     */
    @PutMapping("/orders/{orderId}/status")
    ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderDto.OrderStatusUpdateRequest statusUpdateRequest);
    
    /**
     * Get order by ID
     */
    @GetMapping("/orders/{orderId}")
    ResponseEntity<OrderDto.OrderResponse> getOrderById(@PathVariable Long orderId);
    
    /**
     * Get order by ID and user ID (for security)
     */
    @GetMapping("/orders/{orderId}/user/{userId}")
    ResponseEntity<OrderDto.OrderResponse> getOrderByIdAndUserId(
            @PathVariable Long orderId,
            @PathVariable Long userId);
}
