package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.ProductRequest;
import com.techbytedev.warehousemanagement.dto.request.UpdateMinStockRequest;
import com.techbytedev.warehousemanagement.dto.response.ProductDetailResponse;
import com.techbytedev.warehousemanagement.dto.response.ResultPaginationDTO;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.service.ProductService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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

    // Count all products
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(productService.countAllProducts());
    }

    // Get product details by code
    @GetMapping("/{productCode}")
    public ResponseEntity<?> getProductDetail(@PathVariable String productCode) {
        try {
            ProductDetailResponse response = productService.getProductDetailByCode(productCode);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching product with code: {}, error: {}", productCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get all products with pagination
    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'GET')")
    public ResponseEntity<ResultPaginationDTO> getAllProducts(Pageable pageable) {
        try {
            ResultPaginationDTO response = productService.getAllProducts(pageable);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching paginated products: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Create a new product
    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'POST')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            Product createdProduct = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (RuntimeException e) {
            logger.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update an existing product
    @PutMapping("/{productCode}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'PUT')")
    public ResponseEntity<?> updateProduct(
            @PathVariable String productCode,
            @Valid @RequestBody ProductRequest request) {
        try {
            Product updatedProduct = productService.updateProduct(productCode, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            logger.error("Error updating product with code: {}, error: {}", productCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Update minStock for a product
    @PutMapping("/{productCode}/min-stock")
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete a product
    @DeleteMapping("/{productCode}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'DELETE')")
    public ResponseEntity<?> deleteProduct(@PathVariable String productCode) {
        try {
            productService.deleteProduct(productCode);
            return ResponseEntity.ok("Sản phẩm đã được xóa thành công!");
        } catch (RuntimeException e) {
            logger.error("Error deleting product with code: {}, error: {}", productCode, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Batch update minStock
    @PutMapping("/min-stock/batch")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'PUT')")
    public ResponseEntity<String> updateBatchMinStock(@RequestBody List<Map<String, Object>> updates) {
        try {
            productService.updateBatchMinStock(updates);
            return ResponseEntity.ok("Cập nhật hàng loạt mức tồn kho thành công!");
        } catch (RuntimeException e) {
            logger.error("Error updating batch minStock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}