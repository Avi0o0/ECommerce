package com.ecom.userservice.dto;

import com.ecom.userservice.entity.RoleName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank @Email String email,
    @Size(max = 128) String firstName,
    @Size(max = 128) String lastName,
    String address,
    RoleName role
) {}
