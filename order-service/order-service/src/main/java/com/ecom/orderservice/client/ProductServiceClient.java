package com.ecom.orderservice.client;

import com.ecom.orderservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceClient {
    
    /**
     * Get product details by ID
     */
    @GetMapping("/products/{id}")
    ResponseEntity<ProductDto.ProductResponse> getProductById(@PathVariable Long id);
    
    /**
     * Get multiple products by IDs
     */
    @PostMapping("/products/batch")
    ResponseEntity<List<ProductDto.ProductResponse>> getProductsByIds(@RequestBody List<Long> productIds);
    
    /**
     * Check product availability
     */
    @GetMapping("/products/{id}/availability")
    ResponseEntity<Boolean> checkProductAvailability(@PathVariable Long id);
}
