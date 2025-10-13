package com.ecom.userservice.dto;

import com.ecom.userservice.entity.RoleName;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    RoleName role
) {}
