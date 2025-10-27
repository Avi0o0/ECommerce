package com.ecom.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthenticationFilter authenticationFilter) {
        return builder.routes()
                // Auth Service - Public access
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                
                // User Service - Requires authentication for user management
                .route("user-service", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://USER-SERVICE")
                )
                
                // Product Service - Requires authentication for admin operations
                .route("product-service", r -> r
                        .path("/products/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://PRODUCT-SERVICE")
                )
                
                // Order Service - Requires authentication
                .route("order-service", r -> r
                        .path("/orders/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://ORDER-SERVICE")
                )
                
                // Payment Service - Requires authentication
                .route("payment-service", r -> r
                        .path("/payments/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://PAYMENT-SERVICE")
                )
                
                // Cart Service - Requires authentication
                .route("cart-service", r -> r
                        .path("/cart/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
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