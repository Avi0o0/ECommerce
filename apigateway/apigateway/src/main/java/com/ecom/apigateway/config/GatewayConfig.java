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
                
                // User Service
                .route("user-service", r -> r
                        .path("/users/**")
                        .uri("lb://USER-SERVICE")
                )
                
                // Product Service
                .route("product-service", r -> r
                        .path("/products/**")
                        .uri("lb://PRODUCT-SERVICE")
                )
                
                // Order Service
                .route("order-service", r -> r
                        .path("/orders/**")
                        .uri("lb://ORDER-SERVICE")
                )
                
                // Payment Service
                .route("payment-service", r -> r
                        .path("/payments/**")
                        .uri("lb://PAYMENT-SERVICE")
                )
                
                // Cart Service
                .route("cart-service", r -> r
                        .path("/cart/**")
                        .uri("lb://CART-SERVICE")
                )
                
                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri("lb://INVENTORY-SERVICE")
                )
                
                // Notification Service
                .route("notification-service", r -> r
                        .path("/notifications/**")
                        .uri("lb://NOTIFICATION-SERVICE")
                )
                
                .build();
    }
}