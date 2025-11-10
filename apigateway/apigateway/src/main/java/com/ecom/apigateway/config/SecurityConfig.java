//package com.ecom.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.authorization.AuthorizationDecision;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authorization.AuthorizationContext;
//
//import jwt.util.JwtTokenUtil;
//import reactor.core.publisher.Mono;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//	@Bean
//    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors(ServerHttpSecurity.CorsSpec::disable)
//                .authorizeExchange(auth -> auth
//                    .pathMatchers("/auth/**", "/actuator/**").permitAll()
//                    .anyExchange().access(this::customAuthorization)
//                )
//                .build();
//    }
//
//	private Mono<AuthorizationDecision> customAuthorization(Mono<Authentication> authentication,
//			AuthorizationContext context) {
//		ServerHttpRequest request = context.getExchange().getRequest();
//		String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//		if (token != null && token.startsWith("Bearer ")) {
//			try {
//				JwtTokenUtil.isTokenValid(token.substring(7));
//				return Mono.just(new AuthorizationDecision(true));
//			} catch (Exception ex) {
//				return Mono.just(new AuthorizationDecision(false));
//			}
//		}
//		return Mono.just(new AuthorizationDecision(false));
//	}
//}