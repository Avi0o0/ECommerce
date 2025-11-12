package com.ecom.userservice.security;

import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	@Value("${security.jwt.secret}")
	private String secret;

	@Value("${security.jwt.expiration-seconds:1800}")
	private long expirationSeconds;

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(UserDetails userDetails) {
		Instant now = Instant.now();
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		return Jwts.builder().setSubject(userDetails.getUsername()).addClaims(Map.of("roles", roles))
				.setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(expirationSeconds)))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	//@SuppressWarnings("unchecked")
	public List<String> extractRoles(String token) {
	    Object value = extractAllClaims(token).get("roles");
	    if (value instanceof List<?>) {
	        return ((List<?>) value).stream()
	                .map(String::valueOf)
	                .toList();
	    }
	    return Collections.emptyList();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		Date exp = extractAllClaims(token).getExpiration();
		return username.equals(userDetails.getUsername()) && exp.after(new Date());
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractAllClaims(token);
			Date exp = claims.getExpiration();
			return exp.after(new Date());
		} catch (Exception e) {
			logger.warn("Token validation failed: {}", e.getMessage(), e);
			return false;
		}
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}