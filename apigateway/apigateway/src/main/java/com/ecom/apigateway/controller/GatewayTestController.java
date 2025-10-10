package com.ecom.apigateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
public class GatewayTestController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayTestController.class);

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        logger.info("Gateway health check requested");
        return Mono.just(ResponseEntity.ok("API Gateway is healthy"));
    }

    @GetMapping("/info")
    public Mono<ResponseEntity<GatewayInfo>> info(ServerWebExchange exchange) {
        logger.info("Gateway info requested");
        
        GatewayInfo info = new GatewayInfo(
            "API Gateway",
            "1.0.0",
            "Single entry point for E-Commerce microservices",
            exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown"
        );
        
        return Mono.just(ResponseEntity.ok(info));
    }

    @GetMapping("/test-auth")
    public Mono<ResponseEntity<String>> testAuth(ServerWebExchange exchange) {
        String username = exchange.getRequest().getHeaders().getFirst("X-Username");
        String roles = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
        
        logger.info("Test auth endpoint accessed by user: {} with roles: {}", username, roles);
        
        String message = String.format("Authentication successful! User: %s, Roles: %s", username, roles);
        return Mono.just(ResponseEntity.ok(message));
    }

    public static class GatewayInfo {
        private String name;
        private String version;
        private String description;
        private String clientIp;

        public GatewayInfo(String name, String version, String description, String clientIp) {
            this.name = name;
            this.version = version;
            this.description = description;
            this.clientIp = clientIp;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }
    }
}
