package com.ecom.cartservice.dto;

public class GlobalErrorResponse {
    
    private Integer status;
    private String message;
    private String desc;
    
    // Constructors
    public GlobalErrorResponse() {}
    
    public GlobalErrorResponse(Integer status, String message, String desc) {
        this.status = status;
        this.message = message;
        this.desc = desc;
    }
    
    // Getters and Setters
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return "GlobalErrorResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
