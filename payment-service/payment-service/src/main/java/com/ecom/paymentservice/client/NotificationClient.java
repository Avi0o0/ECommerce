package com.ecom.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.paymentservice.dto.NotificationRequest;
import com.ecom.paymentservice.dto.NotificationResponse;

import jakarta.validation.Valid;

@FeignClient(name = "notification-service")
public interface NotificationClient {

	@PostMapping("/notifications")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request);
}
