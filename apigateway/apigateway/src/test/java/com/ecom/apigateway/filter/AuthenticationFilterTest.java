package com.ecom.apigateway.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import com.ecom.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    private AuthenticationFilter authenticationFilter;

    @Mock
    private GatewayFilterChain filterChain;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        authenticationFilter = new AuthenticationFilter();
        authenticationFilter.jwtUtil = jwtUtil;
    }

    @Test
    @DisplayName("Should pass request when path is not secured")
    void shouldPassRequest_whenPathIsNotSecured() {
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/auth/login").build()
        );
        
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());
        
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return unauthorized when no auth header is present")
    void shouldReturnUnauthorized_whenNoAuthHeaderPresent() {
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/products").build()
        );
        
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        StepVerifier.create(result)
            .expectComplete()
            .verify();
            
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    @DisplayName("Should pass request when valid token is provided")
    void shouldPassRequest_whenValidTokenProvided() {
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid.token.here")
                .build()
        );
        
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return unauthorized when invalid token is provided")
    void shouldReturnUnauthorized_whenInvalidTokenProvided() {
        ServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/api/products")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here")
                .build()
        );
        
        when(jwtUtil.validateToken(anyString())).thenThrow(new RuntimeException("Invalid token"));
        
        GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        StepVerifier.create(result)
            .expectComplete()
            .verify();
            
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
    }
}