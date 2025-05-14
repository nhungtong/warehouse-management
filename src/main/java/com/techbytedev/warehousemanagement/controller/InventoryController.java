package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @GetMapping("/count")
    public long count() {
        return inventoryService.getTotalInventory();
    }
    @GetMapping("/daily-inventory")
    public List<Object[]> getDailyInventory() {
        return inventoryService.getAllInvetoryCurrentMonth();
    }
}
