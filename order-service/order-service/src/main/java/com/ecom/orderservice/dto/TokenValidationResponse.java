package com.ecom.orderservice.dto;

import java.util.List;

public class TokenValidationResponse {
    private String token;
    private String username;
    private String userId;
    private List<String> roles;
    private boolean valid;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(String token, String username, String userId, List<String> roles, boolean valid) {
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
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
