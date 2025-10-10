package com.ecom.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class RoleBasedAuthorizationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RoleBasedAuthorizationFilter.class);
    private static final String ROLES_HEADER = "X-User-Roles";

    private final ObjectMapper objectMapper;

    public RoleBasedAuthorizationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Define role requirements for different endpoints
    private static final List<RoleRequirement> ROLE_REQUIREMENTS = Arrays.asList(
        // Product Service - Admin only for CUD operations
        new RoleRequirement("/api/products", "POST", "ADMIN"),
        new RoleRequirement("/api/products", "PUT", "ADMIN"),
        new RoleRequirement("/api/products", "DELETE", "ADMIN"),
        
        // Order Service - Both USER and ADMIN can access
        new RoleRequirement("/api/orders", "GET", "USER", "ADMIN"),
        new RoleRequirement("/api/orders", "POST", "USER", "ADMIN"),
        new RoleRequirement("/api/orders", "PUT", "USER", "ADMIN"),
        
        // Payment Service - Both USER and ADMIN can access
        new RoleRequirement("/api/payments", "GET", "USER", "ADMIN"),
        new RoleRequirement("/api/payments", "POST", "USER", "ADMIN"),
        
        // Cart Service - Both USER and ADMIN can access
        new RoleRequirement("/api/cart", "GET", "USER", "ADMIN"),
        new RoleRequirement("/api/cart", "POST", "USER", "ADMIN"),
        new RoleRequirement("/api/cart", "PUT", "USER", "ADMIN"),
        new RoleRequirement("/api/cart", "DELETE", "USER", "ADMIN"),
        
        // User Service - Both USER and ADMIN can access GET, ADMIN only for DELETE
        new RoleRequirement("/users", "GET", "USER", "ADMIN"),
        new RoleRequirement("/users", "PUT", "USER", "ADMIN"),
        new RoleRequirement("/users", "DELETE", "ADMIN"),
        
        // User Service API version - Both USER and ADMIN can access GET, ADMIN only for DELETE
        new RoleRequirement("/api/users", "GET", "USER", "ADMIN"),
        new RoleRequirement("/api/users", "PUT", "USER", "ADMIN"),
        new RoleRequirement("/api/users", "DELETE", "ADMIN"),
        
        // Notification Service - Admin only
        new RoleRequirement("/api/notifications", "GET", "ADMIN"),
        new RoleRequirement("/api/notifications", "POST", "ADMIN"),
        new RoleRequirement("/api/notifications", "PUT", "ADMIN"),
        new RoleRequirement("/api/notifications", "DELETE", "ADMIN"),
        
        // Inventory Service - Admin only
        new RoleRequirement("/api/inventory", "GET", "ADMIN"),
        new RoleRequirement("/api/inventory", "POST", "ADMIN"),
        new RoleRequirement("/api/inventory", "PUT", "ADMIN"),
        new RoleRequirement("/api/inventory", "DELETE", "ADMIN")
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        logger.debug("Checking authorization for {} {} by user with roles: {}", 
                method, path, request.getHeaders().getFirst(ROLES_HEADER));

        // Skip authorization for public endpoints
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint, skipping authorization: {}", path);
            return chain.filter(exchange);
        }

        // Get user roles from headers (set by JwtAuthenticationFilter)
        String rolesHeader = request.getHeaders().getFirst(ROLES_HEADER);
        if (rolesHeader == null || rolesHeader.trim().isEmpty()) {
            logger.warn("No roles found in headers for path: {}", path);
            return handleForbidden(exchange, "No user roles found");
        }

        List<String> userRoles = Arrays.asList(rolesHeader.split(","));
        logger.debug("User roles: {}", userRoles);

        // Check if user has required role for this endpoint
        if (!hasRequiredRole(path, method, userRoles)) {
            logger.warn("Access denied for {} {} - user roles: {} do not match requirements", 
                    method, path, userRoles);
            return handleForbidden(exchange, "Insufficient permissions");
        }

        logger.debug("Authorization successful for {} {}", method, path);
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") || 
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/") ||
               path.equals("/health");
    }

    private boolean hasRequiredRole(String path, String method, List<String> userRoles) {
        for (RoleRequirement requirement : ROLE_REQUIREMENTS) {
            if (requirement.matches(path, method)) {
                logger.debug("Found matching role requirement: {} {} requires {}", 
                        requirement.path, requirement.method, requirement.requiredRoles);
                
                // Check if user has any of the required roles
                for (String requiredRole : requirement.requiredRoles) {
                    if (userRoles.contains(requiredRole) || userRoles.contains("ROLE_" + requiredRole)) {
                        return true;
                    }
                }
                return false;
            }
        }

        // If no specific requirement found, allow access (for endpoints like GET /api/products)
        logger.debug("No specific role requirement found for {} {}, allowing access", method, path);
        return true;
    }

    private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    message,
                    System.currentTimeMillis()
            );

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            
            logger.warn("Returning 403 Forbidden: {}", message);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            logger.error("Error creating error response", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -99; // Run after JWT authentication filter
    }

    private static class RoleRequirement {
        private final String path;
        private final String method;
        private final String[] requiredRoles;

        public RoleRequirement(String path, String method, String... requiredRoles) {
            this.path = path;
            this.method = method;
            this.requiredRoles = requiredRoles;
        }

        public boolean matches(String requestPath, String requestMethod) {
            return requestPath.startsWith(path) && method.equals(requestMethod);
        }
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
