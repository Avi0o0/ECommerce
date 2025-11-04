package com.ecom.userservice.dto;

import java.util.List;
import java.util.UUID;

public class TokenValidationResponse {
    private String token;
    private String username;
    private UUID userId;
    private List<String> roles;
    private boolean valid;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(String token, String username, UUID userId, List<String> roles, boolean valid) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.roles = roles;
        this.valid = valid;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
