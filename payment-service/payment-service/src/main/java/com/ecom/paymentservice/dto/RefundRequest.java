package com.ecom.paymentservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RefundRequest {
    
    @NotNull(message = "Payment ID is required")
    @Positive(message = "Payment ID must be positive")
    private Long paymentId;
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal refundAmount;
    
    @NotBlank(message = "Refund reason is required")
    private String reason;
    
    // Constructors
    public RefundRequest() {}
    
    public RefundRequest(Long paymentId, BigDecimal refundAmount, String reason) {
        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @Override
    public String toString() {
        return "RefundRequest{" +
                "paymentId=" + paymentId +
                ", refundAmount=" + refundAmount +
                ", reason='" + reason + '\'' +
                '}';
    }
}
