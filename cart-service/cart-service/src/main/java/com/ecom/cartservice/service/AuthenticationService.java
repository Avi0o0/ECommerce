package com.ecom.cartservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ecom.cartservice.constants.CartServiceConstants;

import jwt.util.JwtTokenUtil;

@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
      
    public String getUsername(String bearerToken) {
        try {
            return JwtTokenUtil.extractUsername(bearerToken);
        } catch (Exception e) {
            logger.error(CartServiceConstants.LOG_ERROR_GETTING_USERNAME, e.getMessage(), e);
            return null;
        }
    }
}
