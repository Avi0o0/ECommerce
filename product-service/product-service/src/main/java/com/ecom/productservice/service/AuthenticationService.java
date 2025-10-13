package com.ecom.productservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecom.productservice.client.UserServiceClient;
import com.ecom.productservice.dto.TokenRequest;
import com.ecom.productservice.dto.TokenValidationResponse;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserServiceClient userServiceClient;
    
    public AuthenticationService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }
    
    public boolean isAdmin(String bearerToken) {
        try {
            logger.info("Validating token with User Service");
            
            // Extract token from "Bearer <token>" format
            String token = bearerToken.startsWith("Bearer ") ? 
                bearerToken.substring(7) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info("Token validation response: valid={}, username={}, roles={}", 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains("ROLE_ADMIN");
            
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean isValidToken(String bearerToken) {
        try {
            String token = bearerToken.startsWith("Bearer ") ? 
                bearerToken.substring(7) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            return response.isValid();
            
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }
}
