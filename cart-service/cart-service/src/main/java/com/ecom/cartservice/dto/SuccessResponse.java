package com.ecom.cartservice.dto;

public class SuccessResponse {
    private int status;
    private String message;
    
    public SuccessResponse() {}
    
    public SuccessResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
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
}
