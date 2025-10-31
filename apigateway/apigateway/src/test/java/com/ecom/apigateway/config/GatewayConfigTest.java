package com.ecom.apigateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayConfigTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Should route to auth service for public endpoints")
    void shouldRouteToAuthService_forPublicEndpoints() {
        webClient.get()
                .uri("/auth/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Should require authentication for protected endpoints")
    void shouldRequireAuth_forProtectedEndpoints() {
        webClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Should allow access to actuator endpoints")
    void shouldAllowAccess_toActuatorEndpoints() {
        webClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }
}