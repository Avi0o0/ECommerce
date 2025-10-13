package com.ecom.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyPasswordRequest {
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Constructors
    public VerifyPasswordRequest() {}
    
    public VerifyPasswordRequest(String password) {
        this.password = password;
    }
    
    // Getters and Setters
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
