package com.ecom.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.cartservice.dto.TokenRequest;
import com.ecom.cartservice.dto.TokenValidationResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    
    @PostMapping("/auth/validate")
    TokenValidationResponse validateToken(@RequestBody TokenRequest request);
}
