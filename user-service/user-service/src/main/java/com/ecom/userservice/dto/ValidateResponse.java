package com.ecom.userservice.dto;

import java.util.List;

public record ValidateResponse(
    String username,
    List<String> roles,
    long expiresAt
) {}
