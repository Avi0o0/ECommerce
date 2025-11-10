package jwt.util;

import java.security.Key;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jwt.util.service.PropertiesHandler;

public class JwtTokenUtil {

	private static String secret = PropertiesHandler.getProperty("jwt-secret");

	private static Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public static String extractUsername(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public static boolean isTokenValid(String token) {
		try {
			Claims claims = extractAllClaims(token);
			Date exp = claims.getExpiration();
			return exp.after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	public static Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public static List<String> extractRoles(String token) {
		Object value = extractAllClaims(token).get("roles");

		if (value instanceof List<?>) {
			return ((List<?>) value).stream().map(Object::toString).toList();
		}

		return List.of();
	}
}
