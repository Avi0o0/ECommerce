package com.ecom.userservice;

import java.util.Date;

import com.ecom.userservice.security.JwtService;

import io.jsonwebtoken.Claims;

public class JWTTests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JwtService jwtService = new JwtService();
		
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzYyMjM5NDM4LCJleHAiOjE3NjIyNDEyMzh9.8ntaeMeaMZ9D-m8etGtF_s-4SxmZjllsFq6C4_RmiAs";
		
		Claims claims = jwtService.extractAllClaims(token);
		String username = claims.getSubject();
		String role = claims.get("role", String.class);
		Date expiration = claims.getExpiration();
		
		System.out.println(username);
		System.out.println(role);
		System.out.println(expiration);
	}
}
