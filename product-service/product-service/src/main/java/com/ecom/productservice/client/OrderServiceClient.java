package com.ecom.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecom.productservice.dto.OrderRequest;
import com.ecom.productservice.dto.OrderResponse;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    
    @PostMapping("/orders/checkout")
    OrderResponse checkout(@RequestBody OrderRequest orderRequest, 
                          @RequestHeader("Authorization") String authorization);
}
