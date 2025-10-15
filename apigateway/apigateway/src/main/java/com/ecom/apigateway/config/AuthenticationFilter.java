package com.ecom.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.ecom.apigateway.dto.TokenRequest;
import com.ecom.apigateway.dto.TokenValidationResponse;

/**
 * Authentication filter for protected service routes
 * Validates JWT tokens with User Service before forwarding requests
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Value("${user.service.url:lb://USER-SERVICE}")
    private String userServiceUrl;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            logger.debug("Authentication Filter - Processing request to: {}", path);
            
            // Skip authentication for health checks and actuator endpoints
            if (isPublicEndpoint(path)) {
                logger.debug("Skipping authentication for public endpoint: {}", path);
                return chain.filter(exchange);
            }
            
            // Extract Authorization header
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header for request to: {}", path);
                return handleUnauthorized(exchange, "Authorization header required");
            }
            
            String token = authHeader.substring(7);
            
            // Validate token with User Service
            return validateToken(token)
                    .flatMap(isValid -> {
                        if (isValid) {
                            logger.debug("Token validation successful for request to: {}", path);
                            return chain.filter(exchange);
                        } else {
                            logger.warn("Token validation failed for request to: {}", path);
                            return handleUnauthorized(exchange, "Invalid or expired token");
                        }
                    })
                    .onErrorResume(error -> {
                        logger.error("Error validating token for request to: {} - {}", path, error.getMessage(), error);
                        return handleUnauthorized(exchange, "Token validation failed");
                    });
        };
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/actuator/") || 
               path.contains("/health") || 
               path.contains("/info");
    }

    private Mono<Boolean> validateToken(String token) {
        return WebClient.builder()
                .build()
                .post()
                .uri(userServiceUrl + "/auth/validate")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(new TokenRequest(token))
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .map(response -> response.isValid())
                .onErrorReturn(false);
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        
        String body = String.format("{\"status\":401,\"message\":\"%s\",\"timestamp\":\"%s\"}", 
                message, java.time.LocalDateTime.now());
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}