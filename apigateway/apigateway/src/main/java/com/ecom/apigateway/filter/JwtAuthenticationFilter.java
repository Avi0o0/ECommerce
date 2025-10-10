package com.ecom.apigateway.filter;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ecom.apigateway.service.JwtValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_HEADER = "X-User-Token";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String ROLES_HEADER = "X-User-Roles";

    private final JwtValidationService jwtValidationService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtValidationService jwtValidationService, ObjectMapper objectMapper) {
        this.jwtValidationService = jwtValidationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        logger.debug("Processing request to path: {}", path);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint, skipping authentication: {}", path);
            return chain.filter(exchange);
        }

        // Extract token from Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return handleUnauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        logger.debug("Extracted token for validation");

        // Validate token with UserService
        return jwtValidationService.validateToken(token)
                .flatMap(validation -> {
                    if (validation == null) {
                        logger.warn("Token validation returned null for path: {}", path);
                        return handleUnauthorized(exchange, "Invalid token");
                    }

                    if (jwtValidationService.isTokenExpired(validation)) {
                        logger.warn("Token expired for user: {} on path: {}", validation.getUsername(), path);
                        return handleUnauthorized(exchange, "Token expired");
                    }

                    logger.debug("Token validation successful for user: {} with roles: {}", 
                            validation.getUsername(), validation.getRoles());

                    // Add user information to headers for downstream services
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header(TOKEN_HEADER, token)
                            .header(USERNAME_HEADER, validation.getUsername())
                            .header(ROLES_HEADER, String.join(",", validation.getRoles()))
                            .build();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(error -> {
                    logger.error("Error during token validation for path: {}", path, error);
                    return handleUnauthorized(exchange, "Token validation failed");
                });
    }

    private boolean isPublicEndpoint(String path) {
        // Define public endpoints that don't require authentication
        return path.startsWith("/auth/") || 
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/") ||
               path.equals("/health");
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    message,
                    System.currentTimeMillis()
            );

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            
            logger.warn("Returning 401 Unauthorized: {}", message);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            logger.error("Error creating error response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // High priority to run early in the filter chain
    }

    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private long timestamp;

        public ErrorResponse(int status, String error, String message, long timestamp) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
