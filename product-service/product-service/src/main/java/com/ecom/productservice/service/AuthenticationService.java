package com.ecom.productservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecom.productservice.constants.ProductServiceConstants;
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
            logger.info(ProductServiceConstants.LOG_VALIDATING_TOKEN_WITH_USER_SERVICE);
            
            // Extract token from "Bearer <token>" format
            String token = bearerToken.startsWith(ProductServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(ProductServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(ProductServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains(ProductServiceConstants.ROLE_ADMIN);
            
        } catch (Exception e) {
            logger.error(ProductServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isValidToken(String bearerToken) {
        try {
            String token = bearerToken.startsWith(ProductServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(ProductServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            return response.isValid();
            
        } catch (Exception e) {
            logger.error(ProductServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isUser(String bearerToken) {
        try {
            logger.info(ProductServiceConstants.LOG_VALIDATING_TOKEN_WITH_USER_SERVICE);
            
            String token = bearerToken.startsWith(ProductServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(ProductServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(ProductServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains(ProductServiceConstants.ROLE_USER);
            
        } catch (Exception e) {
            logger.error(ProductServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }
    
    public Long getUserId(String bearerToken) {
        try {
            String token = bearerToken.startsWith(ProductServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(ProductServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            return response.isValid() ? response.getUserId() : null;
            
        } catch (Exception e) {
            logger.error(ProductServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return null;
        }
    }
}
