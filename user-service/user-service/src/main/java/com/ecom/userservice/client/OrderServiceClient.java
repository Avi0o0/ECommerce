package com.ecom.userservice.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecom.userservice.dto.OrderResponse;
import com.ecom.userservice.dto.OrderSummaryResponse;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/orders/user/{userId}")
    List<OrderSummaryResponse> getOrdersByUserId(@PathVariable("userId") UUID userId,
                                                 @RequestHeader("Authorization") String authorization);

    @GetMapping("/orders/{orderId}")
    OrderResponse getOrderById(@PathVariable("orderId") Long orderId,
                               @RequestHeader("Authorization") String authorization);
}
