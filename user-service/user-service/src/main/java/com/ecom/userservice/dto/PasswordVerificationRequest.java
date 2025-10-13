package com.ecom.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordVerificationRequest(
    @NotBlank String password
) {}
