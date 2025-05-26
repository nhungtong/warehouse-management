package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.ProductDetailResponse;
import com.techbytedev.warehousemanagement.dto.response.ProductResponse;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public long countAllProducts() {
        return productRepository.count();
    }

    public ProductResponse getProductByCode(String code) {
        Product product = productRepository.findByProductCode(code).orElse(null);
        if (product == null) {
            return null;
        }
        Integer currentStock = inventoryRepository.sumQuantityByProductId(product.getId());
        return new ProductResponse(product, currentStock);
    }

    public ProductDetailResponse getProductDetailByCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElse(null);
        String locationName = inventory != null ? inventory.getLocation().getName() : "Không xác định";
        Integer quantity = inventory != null ? inventory.getQuantity() : 0;
        return new ProductDetailResponse(
                product.getProductCode(),
                product.getName(),
                product.getUnit(),
                product.getSupplier() != null ? product.getSupplier().getName() : "Không có",
                locationName,
                quantity
        );
    }

    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Cập nhật minStock cho một sản phẩm
    public Product updateMinStock(String productCode, Integer minStock) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        product.setMinStock(minStock);
        return productRepository.save(product);
    }

    // Cập nhật hàng loạt minStock
    public void updateBatchMinStock(List<Map<String, Object>> updates) {
        for (Map<String, Object> update : updates) {
            String productCode = (String) update.get("productCode");
            Integer minStock = (Integer) update.get("minStock");
            Product product = productRepository.findByProductCode(productCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
            product.setMinStock(minStock);
            productRepository.save(product);
        }
    }
}