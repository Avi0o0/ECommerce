package com.ecom.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    
    @PutMapping("/products/reduce-stock")
    void reduceStockByProductId(@RequestParam Long productId, @RequestParam Integer quantity);
}

