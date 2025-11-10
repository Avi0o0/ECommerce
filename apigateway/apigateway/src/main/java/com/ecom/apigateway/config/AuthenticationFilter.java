package com.ecom.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ecom.apigateway.dto.GlobalErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jwt.util.JwtTokenUtil;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	public record Config() {
	}

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			String path = request.getPath().value();

			logger.info("Authentication Filter - Processing request to: {}", path);

			// Skip authentication actuator endpoints
			if (isPublicEndpoint(path)) {
				logger.info("Skipping authentication for public endpoint: {}", path);
				return chain.filter(exchange);
			}

			// Extract Authorization header
			String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				logger.info("Missing or invalid Authorization header for request to: {}", path);
				return handleUnauthorized(exchange, "Authorization header required");
			}

			String token = authHeader.substring(7);

			// Validate token with User Service
			try {
				logger.info("validating token");
				JwtTokenUtil.isTokenValid(token);
			} catch (Exception e) {
				return handleUnauthorized(exchange, "Token validation failed");
			}
			
			return chain.filter(exchange);
		};
	}

	private boolean isPublicEndpoint(String path) {
		return path.contains("/actuator/") || path.contains("/health") || path.contains("/info")
				|| path.contains("/refresh");
	}

	private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {

		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

		try {
			GlobalErrorResponse errorResponse = new GlobalErrorResponse(401, message, message);

			ObjectMapper mapper = new ObjectMapper();
			String body = mapper.writeValueAsString(errorResponse);

			return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
		} catch (Exception e) {
			logger.error("Error creating error response: {}", e.getMessage());
			String fallbackBody = "{\"status\":401,\"message\":\"" + message + "\",\"desc\":\"" + message + "\"}";
			return response.writeWith(Mono.just(response.bufferFactory().wrap(fallbackBody.getBytes())));
		}
	}

}