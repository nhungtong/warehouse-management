package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.UpdateMinStockRequest;
import com.techbytedev.warehousemanagement.dto.response.ProductDetailResponse;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.service.ProductService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/count")
    public long count() {
        return productService.countAllProducts();
    }

    // Hiển thị chi tiết sản phẩm
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productCode) {
        try {
            ProductDetailResponse response = productService.getProductDetailByCode(productCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm");
        }
    }

    // Lấy danh sách sản phẩm
    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'GET')")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Cập nhật minStock cho một sản phẩm
    @PutMapping("/{productCode}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'PUT')")
    public ResponseEntity<?> updateMinStock(
            @PathVariable String productCode,
            @Valid @RequestBody UpdateMinStockRequest request) {
        logger.debug("Received request for productCode: {}, request: {}", productCode, request);
        if (request.getMinStock() == null) {
            logger.warn("minStock is null for productCode: {}", productCode);
            return ResponseEntity.badRequest().body("Lỗi: Trường 'minStock' không được để trống.");
        }
        try {
            Product updatedProduct = productService.updateMinStock(productCode, request.getMinStock());
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            logger.error("Error updating minStock for productCode: {}, error: {}", productCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lỗi: " + e.getMessage());
        }
    }

    // Cập nhật hàng loạt minStock
    @PutMapping("/min-stock/batch")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'PUT')")
    public ResponseEntity<String> updateBatchMinStock(@RequestBody List<Map<String, Object>> updates) {
        productService.updateBatchMinStock(updates);
        return ResponseEntity.ok("Cập nhật hàng loạt mức tồn kho thành công!");
    }
}