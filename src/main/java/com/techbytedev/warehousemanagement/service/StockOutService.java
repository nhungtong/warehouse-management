package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductOutRequest;
import com.techbytedev.warehousemanagement.dto.request.StockOutRequest;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StockOutService {
    private final StockOutDetailRepository stockOutDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockOutFormRepository stockOutFormRepository;
    private final InventoryRepository inventoryRepository;

    public StockOutService(StockOutDetailRepository stockOutDetailRepository, UserRepository userRepository, ProductRepository productRepository, StockOutFormRepository stockOutFormRepository, InventoryRepository inventoryRepository) {
        this.stockOutDetailRepository = stockOutDetailRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockOutFormRepository = stockOutFormRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public long getTotalStockOut() {
        return stockOutDetailRepository.getTotalQuantity();
    }
    @Transactional
    public void handleStockOut(StockOutRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockOutForm stockOutForm = new StockOutForm();
        stockOutForm.setDestination(request.getDestination());
        stockOutForm.setCreatedBy(user);
        stockOutForm.setCreatedAt(LocalDateTime.now());
        stockOutForm.setNote(request.getNote());
        stockOutFormRepository.save(stockOutForm);

        for (ProductOutRequest p : request.getProducts()) {
            Product product = productRepository.findByProductCode(p.getProductCode())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + p.getProductCode()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + p.getProductCode()));

            if (p.getQuantity() > inventory.getQuantity()) {
                throw new RuntimeException("Số lượng xuất vượt quá tồn kho cho sản phẩm: " + p.getProductCode());
            }

            inventory.setQuantity(inventory.getQuantity() - p.getQuantity());
            inventoryRepository.save(inventory);

            StockOutDetail detail = new StockOutDetail();
            detail.setStockOutForm(stockOutForm);
            detail.setProduct(product);
            detail.setQuantity(p.getQuantity());
            stockOutDetailRepository.save(detail);
        }
    }

}
