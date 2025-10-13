package com.ecom.userservice.dto;

import java.util.List;

public class TokenValidationResponse {
    private String token;
    private String username;
    private List<String> roles;
    private boolean valid;
    
    public TokenValidationResponse() {}
    
    public TokenValidationResponse(String token, String username, List<String> roles, boolean valid) {
        this.token = token;
        this.username = username;
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
