package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    public long getTotalInventory() {
        return inventoryRepository.getTotalInventory();
    }
    public List<Object[]> getAllInvetoryCurrentMonth() {
        return inventoryRepository.getInventoryCurrentMonth();
    }

    
}
