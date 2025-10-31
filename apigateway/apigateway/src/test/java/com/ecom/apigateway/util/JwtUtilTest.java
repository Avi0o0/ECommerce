package com.ecom.apigateway.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        jwtUtil.init();
    }

    @Test
    @DisplayName("Should validate token successfully when token is valid")
    void shouldValidateToken_whenTokenIsValid() {
        // Given
        TestJwtUtil testJwtUtil = new TestJwtUtil();
        String token = testJwtUtil.generateToken("testuser", "ROLE_USER");

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void shouldExtractUsername_whenTokenIsValid() {
        // Given
        TestJwtUtil testJwtUtil = new TestJwtUtil();
        String token = testJwtUtil.generateToken("testuser", "ROLE_USER");

        // When
        String username = jwtUtil.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Should extract roles from valid token")
    void shouldExtractRoles_whenTokenIsValid() {
        // Given
        TestJwtUtil testJwtUtil = new TestJwtUtil();
        String token = testJwtUtil.generateToken("testuser", "ROLE_USER,ROLE_ADMIN");

        // When
        String[] roles = jwtUtil.getRolesFromToken(token);

        // Then
        assertArrayEquals(new String[]{"ROLE_USER", "ROLE_ADMIN"}, roles);
    }

    @Test
    @DisplayName("Should return false when token is expired")
    void shouldReturnFalse_whenTokenIsExpired() {
        // Given
        TestJwtUtil testJwtUtil = new TestJwtUtil();
        String token = testJwtUtil.generateExpiredToken("testuser", "ROLE_USER");

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should return false when token is malformed")
    void shouldReturnFalse_whenTokenIsMalformed() {
        // When
        boolean isValid = jwtUtil.validateToken("malformed.token.here");

        // Then
        assertFalse(isValid);
    }
}