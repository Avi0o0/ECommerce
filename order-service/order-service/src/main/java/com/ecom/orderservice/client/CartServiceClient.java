package com.ecom.orderservice.client;

import com.ecom.orderservice.dto.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CART-SERVICE")
public interface CartServiceClient {
    
    /**
     * Get user's cart
     */
    @GetMapping("/cart")
    ResponseEntity<CartDto.CartResponse> getUserCart(@RequestParam Long userId);
    
    /**
     * Clear user's cart
     */
    @DeleteMapping("/cart/clear")
    ResponseEntity<Void> clearUserCart(@RequestParam Long userId);
    
    /**
     * Get cart items count
     */
    @GetMapping("/cart/count")
    ResponseEntity<Integer> getCartItemsCount(@RequestParam Long userId);
}
