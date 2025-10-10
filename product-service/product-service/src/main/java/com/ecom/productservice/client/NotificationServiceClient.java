package com.ecom.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.productservice.dto.NotificationDto;

@FeignClient(name = "notification-service", path = "/notifications")
public interface NotificationServiceClient {

    @PostMapping("/create")
    void createNotification(@RequestBody NotificationDto.NotificationRequest request);
}
