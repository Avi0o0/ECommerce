package com.ecom.userservice.dto;

import java.util.List;

public class UserListResponse {
    private int status;
    private String message;
    private int count;
    private List<UserResponse> users;
    
    public UserListResponse() {}
    
    public UserListResponse(int status, String message, int count, List<UserResponse> users) {
        this.status = status;
        this.message = message;
        this.count = count;
        this.users = users;
    }
    
    // Getters and Setters
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public List<UserResponse> getUsers() {
        return users;
    }
    
    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }
}
