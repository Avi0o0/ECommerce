package com.ecom.orderservice.client;

import com.ecom.orderservice.dto.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {
    
    /**
     * Reserve stock for products
     */
    @PutMapping("/api/inventory/reserve")
    ResponseEntity<InventoryDto.StockOperationResponse> reserveStock(@RequestBody InventoryDto.ReserveRequest request);
    
    /**
     * Release reserved stock
     */
    @PutMapping("/api/inventory/release")
    ResponseEntity<InventoryDto.StockOperationResponse> releaseStock(@RequestBody InventoryDto.ReleaseRequest request);
    
    /**
     * Deduct reserved stock permanently
     */
    @PutMapping("/api/inventory/deduct")
    ResponseEntity<InventoryDto.StockOperationResponse> deductStock(@RequestBody InventoryDto.DeductRequest request);
    
    /**
     * Get inventory for a product
     */
    @GetMapping("/api/inventory/{productId}")
    ResponseEntity<InventoryDto.InventoryResponse> getInventoryByProductId(@PathVariable Long productId);
    
    /**
     * Check if product has sufficient stock
     */
    @GetMapping("/api/inventory/{productId}/check")
    ResponseEntity<Boolean> hasSufficientStock(@PathVariable Long productId, @RequestParam Integer quantity);
}
