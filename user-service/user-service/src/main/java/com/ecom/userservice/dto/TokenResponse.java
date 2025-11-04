package com.ecom.userservice.dto;

import java.util.List;

/**
 * Extended login response: keeps existing token field but adds recentActivities.
 * Clients that only expect the token will still receive it; recentActivities may be null.
 */
public record TokenResponse(String token, List<ProductResponse> recentActivities) {}
