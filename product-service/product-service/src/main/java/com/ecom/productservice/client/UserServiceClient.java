package com.ecom.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.productservice.dto.TokenRequest;
import com.ecom.productservice.dto.TokenValidationResponse;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @PostMapping("/auth/validate")
    TokenValidationResponse validateToken(@RequestBody TokenRequest request);
}
