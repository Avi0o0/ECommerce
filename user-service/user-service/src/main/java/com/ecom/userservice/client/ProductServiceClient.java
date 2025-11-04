package com.ecom.userservice.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.userservice.dto.ProductResponse;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/products/recent/{userId}")
    List<ProductResponse> getRecentProductsForUser(@PathVariable UUID userId,
                                                   @RequestParam(required = false) Integer limit);
}
