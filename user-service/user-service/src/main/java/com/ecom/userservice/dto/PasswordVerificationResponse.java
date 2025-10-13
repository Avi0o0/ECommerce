package com.ecom.userservice.dto;

public record PasswordVerificationResponse(
    boolean matches,
    String username
) {}
