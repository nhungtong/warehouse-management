package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.response.ProductDetailResponse;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.service.ProductService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/count")
    public long count() {
         return productService.countAllProducts();
    }

    // hiển thị chi tiết sản phẩm
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
    public ResponseEntity<Product> updateMinStock(
            @PathVariable String productCode,
            @RequestBody Map<String, Integer> request) {
                System.out.println("Request to update minStock=============================================================================================================================: " + request);
        if (!request.containsKey("minStock")) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(productService.updateMinStock(productCode, request.get("minStock")));
    }

    // Cập nhật hàng loạt minStock
    @PutMapping("/min-stock/batch")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'PUT')")
    public ResponseEntity<String> updateBatchMinStock(@RequestBody List<Map<String, Object>> updates) {
        productService.updateBatchMinStock(updates);
        return ResponseEntity.ok("Cập nhật hàng loạt mức tồn kho thành công!");
    }

}
