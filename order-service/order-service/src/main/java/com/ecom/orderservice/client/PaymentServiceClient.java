package com.ecom.orderservice.client;

import com.ecom.orderservice.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentServiceClient {
    
    /**
     * Process payment for an order
     */
    @PostMapping("/payments/process")
    ResponseEntity<PaymentDto.PaymentResponse> processPayment(@RequestBody PaymentDto.PaymentRequest paymentRequest);
    
    /**
     * Get payment status
     */
    @GetMapping("/payments/{paymentId}/status")
    ResponseEntity<String> getPaymentStatus(@PathVariable String paymentId);
    
    /**
     * Refund payment
     */
    @PostMapping("/payments/{paymentId}/refund")
    ResponseEntity<PaymentDto.PaymentResponse> refundPayment(@PathVariable String paymentId);
}
