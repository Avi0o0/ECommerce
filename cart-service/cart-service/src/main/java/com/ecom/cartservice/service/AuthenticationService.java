package com.ecom.cartservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecom.cartservice.constants.CartServiceConstants;
import com.ecom.cartservice.client.UserServiceClient;
import com.ecom.cartservice.dto.TokenRequest;
import com.ecom.cartservice.dto.TokenValidationResponse;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserServiceClient userServiceClient;
    
    public AuthenticationService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }
    
    public boolean isAdmin(String bearerToken) {
        try {
            logger.info(CartServiceConstants.LOG_VALIDATING_TOKEN_FOR_ADMIN_CHECK);
            
            // Extract token from "Bearer <token>" format
            String token = bearerToken.startsWith(CartServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(CartServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(CartServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains(CartServiceConstants.ROLE_ADMIN);
            
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_VALIDATING_TOKEN_FOR_ADMIN_CHECK, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isValidToken(String bearerToken) {
        try {
            logger.info(CartServiceConstants.LOG_VALIDATING_TOKEN_WITH_USER_SERVICE);
            
            String token = bearerToken.startsWith(CartServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(CartServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(CartServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE_SIMPLE, 
                response.isValid(), response.getUsername());
            
            return response.isValid();
            
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }
    
    public TokenValidationResponse validateTokenAndGetDetails(String bearerToken) {
        try {
            logger.info(CartServiceConstants.LOG_VALIDATING_TOKEN_AND_GETTING_DETAILS);
            
            String token = bearerToken.startsWith(CartServiceConstants.BEARER_PREFIX) ? 
                bearerToken.substring(CartServiceConstants.BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(CartServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response;
            
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_VALIDATING_TOKEN_AND_GETTING_DETAILS, e.getMessage(), e);
            return new TokenValidationResponse(null, null, null, null, false);
        }
    }
    
    public boolean isUser(String bearerToken) {
        try {
            TokenValidationResponse response = validateTokenAndGetDetails(bearerToken);
            return response.isValid() && response.getRoles().contains(CartServiceConstants.ROLE_USER);
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_CHECKING_IF_USER, e.getMessage(), e);
            return false;
        }
    }
    
    public String getUsername(String bearerToken) {
        try {
            TokenValidationResponse response = validateTokenAndGetDetails(bearerToken);
            return response.isValid() ? response.getUsername() : null;
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_GETTING_USERNAME, e.getMessage(), e);
            return null;
        }
    }

    public String getUserId(String bearerToken) {
        try {
            TokenValidationResponse response = validateTokenAndGetDetails(bearerToken);
            return response.isValid() ? response.getUserId() : null;
        } catch (Exception e) {
            logger.error("Error getting user ID from token: {}", e.getMessage(), e);
            return null;
        }
    }
}
