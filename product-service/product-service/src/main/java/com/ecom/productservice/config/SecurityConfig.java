package com.ecom.productservice.config;

import org.springframework.context.annotation.Configuration;

/**
 * Security Configuration for Product Service
 * 
 * Note: This service has NO security configuration because:
 * 1. All authentication is handled by API Gateway
 * 2. All requests are pre-authenticated before reaching this service
 * 3. This follows microservices best practices for centralized security
 */
@Configuration
public class SecurityConfig {
    // No security configuration needed - handled by API Gateway
}