package com.ecom.userservice.dto;

import java.util.List;
import java.util.UUID;

public class TokenValidationResponse {
    private String username;
    private UUID userId;
    private List<String> roles;
    private boolean valid;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(String username, UUID userId, List<String> roles, boolean valid) {
        this.username = username;
        this.userId = userId;
        this.roles = roles;
        this.valid = valid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
