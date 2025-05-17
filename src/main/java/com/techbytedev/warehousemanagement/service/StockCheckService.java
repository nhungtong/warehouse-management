package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.StockCheckRequest;
import com.techbytedev.warehousemanagement.dto.response.StockCheckResponse;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockCheckService {
    private final StockCheckRepository stockCheckRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public StockCheckService(StockCheckRepository stockCheckRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, LocationRepository locationRepository, UserRepository userRepository) {
        this.stockCheckRepository = stockCheckRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    public LocalDateTime getMaxCreateAt()
    {
        return stockCheckRepository.getMaxCreatedAt();
    }

    public StockCheckResponse performStockCheck(StockCheckRequest dto, String username) {

        User checkedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findByProductCode(dto.getProductCode())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Location location = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"))
                .getLocation();

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        int systemQuantity = inventory.getQuantity();
        int actualQuantity = dto.getActualQuantity();

        StockCheck stockCheck = new StockCheck();
        stockCheck.setProduct(product);
        stockCheck.setLocation(location);
        stockCheck.setSystemQuantity(systemQuantity);
        stockCheck.setActualQuantity(actualQuantity);
        stockCheck.setCheckedBy(checkedBy);
        stockCheck.setCreateAt(LocalDateTime.now());

        stockCheckRepository.save(stockCheck);

        return new StockCheckResponse(stockCheck);
    }

    public List<StockCheckResponse> getMonthStockChecks() {
        List<StockCheck> monthChecks = stockCheckRepository.findAllByCurrentMonth();
        return monthChecks.stream()
                .map(StockCheckResponse::new)
                .collect(Collectors.toList());
    }
}
