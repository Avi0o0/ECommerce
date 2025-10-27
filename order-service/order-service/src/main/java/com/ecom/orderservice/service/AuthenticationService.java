package com.ecom.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecom.orderservice.client.UserServiceClient;
import com.ecom.orderservice.constants.OrderServiceConstants;
import com.ecom.orderservice.dto.TokenRequest;
import com.ecom.orderservice.dto.TokenValidationResponse;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final String BEARER_PREFIX = OrderServiceConstants.BEARER_PREFIX;
    private static final int BEARER_TOKEN_START_INDEX = OrderServiceConstants.BEARER_TOKEN_START_INDEX;
    private static final String ROLE_ADMIN = OrderServiceConstants.ROLE_ADMIN;
    private static final String ROLE_USER = OrderServiceConstants.ROLE_USER;
    
    private final UserServiceClient userServiceClient;
    
    public AuthenticationService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }
    
    public boolean isAdmin(String bearerToken) {
        try {
            logger.info(OrderServiceConstants.LOG_VALIDATING_TOKEN_FOR_ADMIN_CHECK);
            
            // Extract token from "Bearer <token>" format
            String token = bearerToken.startsWith(BEARER_PREFIX) ? 
                bearerToken.substring(BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(OrderServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains(ROLE_ADMIN);
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_VALIDATING_TOKEN_FOR_ADMIN_CHECK, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isValidToken(String bearerToken) {
        try {
            logger.info(OrderServiceConstants.LOG_VALIDATING_TOKEN_WITH_USER_SERVICE);
            
            String token = bearerToken.startsWith(BEARER_PREFIX) ? 
                bearerToken.substring(BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(OrderServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE_SIMPLE, 
                response.isValid(), response.getUsername());
            
            return response.isValid();
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_VALIDATING_TOKEN, e.getMessage(), e);
            return false;
        }
    }
    
    public boolean isUser(String bearerToken) {
        try {
            logger.info(OrderServiceConstants.LOG_VALIDATING_TOKEN_FOR_USER_CHECK);
            
            String token = bearerToken.startsWith(BEARER_PREFIX) ? 
                bearerToken.substring(BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(OrderServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response.isValid() && response.getRoles().contains(ROLE_USER);
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_VALIDATING_TOKEN_FOR_USER_CHECK, e.getMessage(), e);
            return false;
        }
    }
    
    public TokenValidationResponse validateTokenAndGetDetails(String bearerToken) {
        try {
            logger.info(OrderServiceConstants.LOG_VALIDATING_TOKEN_AND_GETTING_DETAILS);
            
            String token = bearerToken.startsWith(BEARER_PREFIX) ? 
                bearerToken.substring(BEARER_TOKEN_START_INDEX) : bearerToken;
            
            TokenRequest request = new TokenRequest(token);
            TokenValidationResponse response = userServiceClient.validateToken(request);
            
            logger.info(OrderServiceConstants.LOG_TOKEN_VALIDATION_RESPONSE, 
                response.isValid(), response.getUsername(), response.getRoles());
            
            return response;
            
        } catch (Exception e) {
            logger.error(OrderServiceConstants.LOG_ERROR_VALIDATING_TOKEN_AND_GETTING_DETAILS, e.getMessage(), e);
            return new TokenValidationResponse(null, null, null, null, false);
        }
    }
}
