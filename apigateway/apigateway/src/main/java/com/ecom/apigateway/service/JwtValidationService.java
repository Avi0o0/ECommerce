package com.ecom.apigateway.service;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

@Service
public class JwtValidationService {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationService.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;

    public JwtValidationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8081")
                .build();
    }

    public static record TokenResponse(String token) {}

    public static class TokenValidationResponse {
        private String username;
        private List<String> roles;
        private long expiresAt;

        public TokenValidationResponse() {}

        public TokenValidationResponse(String username, List<String> roles, long expiresAt) {
            this.username = username;
            this.roles = roles;
            this.expiresAt = expiresAt;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() / 1000 > expiresAt;
        }
    }

    public Mono<TokenValidationResponse> validateToken(String token) {
        logger.debug("Validating token with UserService");
        
        return webClient.post()
                .uri("/auth/validate")
                .bodyValue(new TokenResponse(token))
                .retrieve()
                .bodyToMono(TokenValidationResponse.class)
                .timeout(TIMEOUT)
                .doOnSuccess(response -> {
                    logger.debug("Token validation successful for user: {}", response.getUsername());
                })
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) error;
                        logger.warn("Token validation failed with status: {}, response: {}", 
                                ex.getStatusCode(), ex.getResponseBodyAsString());
                    } else {
                        logger.error("Token validation failed with error", error);
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Failed to validate token", error);
                    return Mono.empty();
                });
    }

    public boolean hasRequiredRole(TokenValidationResponse validation, String requiredRole) {
        if (validation == null || validation.getRoles() == null) {
            return false;
        }
        
        return validation.getRoles().contains(requiredRole) || 
               validation.getRoles().contains("ROLE_" + requiredRole);
    }

    public boolean isTokenExpired(TokenValidationResponse validation) {
        return validation == null || validation.isExpired();
    }
}
