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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @Value("${qrcode.base-url:file:///path/to/your/project}") // Giá trị mặc định, thay bằng đường dẫn thực tế
    private String qrCodeBaseUrl; // Inject base URL từ cấu hình

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
            Product updatedProduct = productService.updateProduct(productCode, request); // Truyền cả request
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

    // // Get QR code for a product
    // @GetMapping("/{productCode}/qr-code")
    // @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/products/**', 'GET')")
    // public ResponseEntity<?> getQrCode(@PathVariable String productCode) {
    //     try {
    //         // Fetch the QR code URL from the service
    //         String qrCodeUrl = productService.getQrCodeByProductCode(productCode);
    //         if (qrCodeUrl == null || qrCodeUrl.isEmpty()) {
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                     .body("QR code URL not found for product: " + productCode);
    //         }

    //         // Thêm base URL nếu qrCodeUrl là đường dẫn tương đối
    //         if (!qrCodeUrl.startsWith("http://") && !qrCodeUrl.startsWith("https://") && !qrCodeUrl.startsWith("file://")) {
    //             // Đảm bảo không có dấu gạch chéo kép khi nối
    //             qrCodeUrl = qrCodeBaseUrl.replaceAll("/$", "") + "/" + qrCodeUrl.replaceAll("^/", "");
    //         }

    //         logger.debug("Generated QR code URL: {}", qrCodeUrl); // Log để debug

    //         // Kiểm tra định dạng URL
    //         try {
    //             new URL(qrCodeUrl).toURI(); // Validate URL format
    //         } catch (Exception e) {
    //             logger.error("Invalid QR code URL for productCode: {}, url: {}", productCode, qrCodeUrl);
    //             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    //                     .body("Invalid QR code URL for product: " + productCode);
    //         }

    //         // Create a resource from the URL
    //         UrlResource resource = new UrlResource(new URL(qrCodeUrl));

    //         // Kiểm tra xem tài nguyên có tồn tại không
    //         if (!resource.exists() || !resource.isReadable()) {
    //             logger.error("QR code resource not found or inaccessible for productCode: {}, url: {}", productCode, qrCodeUrl);
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                     .body("QR code resource not found or inaccessible for product: " + productCode);
    //         }

    //         // Return the image with proper headers for download
    //         return ResponseEntity.ok()
    //                 .contentType(MediaType.IMAGE_PNG) // Có thể thay đổi nếu định dạng khác
    //                 .header("Content-Disposition", "attachment; filename=\"qr-code-" + productCode + ".png\"")
    //                 .body(resource);
    //     } catch (Exception e) {
    //         logger.error("Error fetching QR code for productCode: {}, error: {}", productCode, e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Error retrieving QR code: " + e.getMessage());
    //     }
    // }
}