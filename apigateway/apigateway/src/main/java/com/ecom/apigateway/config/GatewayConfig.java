package com.ecom.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service - Public access
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                
                // User Management - Protected with role-based access
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://USER-SERVICE")
                )
                
                // User Management - Legacy route (without /api prefix)
                .route("user-service-legacy", r -> r
                        .path("/users/**")
                        .filters(f -> f.stripPrefix(0)) // Keep the /users prefix
                        .uri("lb://USER-SERVICE")
                )
                
                // Product Service - Protected with role-based access
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://PRODUCT-SERVICE")
                )
                
                // Order Service - Protected with role-based access
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://ORDER-SERVICE")
                )
                
                // Payment Service - Protected with role-based access
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://PAYMENT-SERVICE")
                )
                
                // Cart Service - Protected with role-based access
                .route("cart-service", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://CART-SERVICE")
                )
                
                // Inventory Service - Protected with role-based access
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://INVENTORY-SERVICE")
                )
                
                // Notification Service - Protected with role-based access
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f.stripPrefix(2)) // Remove /api prefix
                        .uri("lb://NOTIFICATION-SERVICE")
                )
                
                // Legacy routes for backward compatibility (without /api prefix)
                .route("product-service-legacy", r -> r
                        .path("/products/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /products prefix
                        .uri("lb://PRODUCT-SERVICE")
                )
                
                .route("order-service-legacy", r -> r
                        .path("/orders/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /orders prefix
                        .uri("lb://ORDER-SERVICE")
                )
                
                .route("payment-service-legacy", r -> r
                        .path("/payments/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /payments prefix
                        .uri("lb://PAYMENT-SERVICE")
                )
                
                .route("cart-service-legacy", r -> r
                        .path("/cart/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /cart prefix
                        .uri("lb://CART-SERVICE")
                )
                
                .route("inventory-service-legacy", r -> r
                        .path("/inventory/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /inventory prefix
                        .uri("lb://INVENTORY-SERVICE")
                )
                
                .route("notification-service-legacy", r -> r
                        .path("/notifications/**")
                        .filters(f -> f.stripPrefix(1)) // Remove /notifications prefix
                        .uri("lb://NOTIFICATION-SERVICE")
                )
                
                .build();
    }
}