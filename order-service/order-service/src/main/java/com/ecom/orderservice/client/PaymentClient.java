package com.ecom.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.orderservice.dto.PaymentRequest;
import com.ecom.orderservice.dto.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    
    @PostMapping("/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
