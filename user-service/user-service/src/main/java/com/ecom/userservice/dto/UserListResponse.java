package com.ecom.userservice.dto;

import java.util.List;
import com.ecom.userservice.entity.UserAccount;

public class UserListResponse {
    private int status;
    private String message;
    private int count;
    private List<UserAccount> users;
    
    public UserListResponse() {}
    
    public UserListResponse(int status, String message, int count, List<UserAccount> users) {
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
    
    public List<UserAccount> getUsers() {
        return users;
    }
    
    public void setUsers(List<UserAccount> users) {
        this.users = users;
    }
}
