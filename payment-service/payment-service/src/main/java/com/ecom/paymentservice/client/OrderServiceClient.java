package com.ecom.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.paymentservice.dto.OrderDto;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {
    
    @GetMapping("/orders/{orderId}/user/{userId}")
    ResponseEntity<OrderDto.OrderResponse> getOrderByIdAndUserId(@PathVariable Long orderId, @PathVariable Long userId);
    
    @PutMapping("/orders/{orderId}/status")
    ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderDto.OrderStatusUpdateRequest statusUpdate);
}
