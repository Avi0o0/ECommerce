package com.ecom.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.cartservice.dto.OrderRequest;
import com.ecom.cartservice.dto.OrderResponse;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {
    
    @PostMapping("/orders/create")
    OrderResponse createOrder(@RequestBody OrderRequest orderRequest);
}
