package com.ecom.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecom.productservice.dto.InventoryDto;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory")
    void createInventory(@RequestBody InventoryDto.InventoryRequest request);

    @PostMapping("/api/inventory/adjust")
    void adjustStock(@RequestBody InventoryDto.AdjustRequest request);
}
