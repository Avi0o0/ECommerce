package jwt.util;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jwt.util.service.PropertiesHandler;

public class JwtTokenUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

	private static String secret = PropertiesHandler.getProperty("jwt-secret");

	private static Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static boolean validateToken(String token) {
		try {
			logger.info("Validating Token");
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			logger.info("Exception caught while validating token: {}", e.getMessage());
			return false;
		}
	}

	public static String extractUsername(String token) {
		logger.info("Extracting username from token");
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public static boolean isTokenValid(String token) {
		try {
			logger.info("Validating Token");
			Claims claims = extractAllClaims(token);
			Date exp = claims.getExpiration();
			return exp.after(new Date());
		} catch (Exception e) {
			logger.info("Exception caught while validating token: {}", e.getMessage());
			return false;
		}
	}

	public Date extractExpiration(String token) {
		logger.info("Extracting expiration date from token");
		return extractAllClaims(token).getExpiration();
	}

	public static Claims extractAllClaims(String token) {
		logger.info("Fetching Claims from token");
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public static List<String> extractRoles(String token) {

		logger.info("Extracting roles from token");
		Object value = extractAllClaims(token).get("roles");

		if (value instanceof List<?>) {
			return ((List<?>) value).stream().map(Object::toString).toList();
		}

		return List.of();
	}
}
