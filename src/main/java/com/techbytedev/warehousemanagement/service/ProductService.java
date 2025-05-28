package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductRequest;
import com.techbytedev.warehousemanagement.dto.response.ProductDetailResponse;
import com.techbytedev.warehousemanagement.dto.response.ProductListResponse;
import com.techbytedev.warehousemanagement.dto.response.ProductResponse;
import com.techbytedev.warehousemanagement.dto.response.ResultPaginationDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.entity.Supplier;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.ProductRepository;
import com.techbytedev.warehousemanagement.repository.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository,
            SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.supplierRepository = supplierRepository;
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

    @Transactional
    public Product createProduct(ProductRequest request) {
        if (productRepository.findByProductCode(request.getProductCode()).isPresent()) {
            throw new RuntimeException("Sản phẩm với mã " + request.getProductCode() + " đã tồn tại");
        }
        Supplier supplier = supplierRepository
                .findById(request.getSupplierId() != null ? request.getSupplierId().intValue() : null)
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId()));

        Product product = new Product();
        product.setProductCode(request.getProductCode());
        product.setName(request.getName());
        product.setUnit(request.getUnit());
        product.setSupplier(supplier);
        product.setMinStock(request.getMinStock() != null ? request.getMinStock() : 0);
        product.setExpirationDate(request.getExpirationDate());
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String productCode, ProductRequest request) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        Integer supplierId = request.getSupplierId() != null ? request.getSupplierId().intValue() : null;
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy nhà cung cấp với ID: " + request.getSupplierId()));

        product.setName(request.getName());
        product.setUnit(request.getUnit());
        product.setSupplier(supplier);
        product.setMinStock(request.getMinStock() != null ? request.getMinStock() : product.getMinStock());
        product.setExpirationDate(request.getExpirationDate());
        
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        if (inventoryRepository.existsByProductId(product.getId())) {
            throw new RuntimeException("Không thể xóa sản phẩm đang có trong kho");
        }
        productRepository.delete(product);
    }

    public ProductDetailResponse getProductDetailByCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElse(null);
        String locationName = inventory != null ? inventory.getLocation().getName() : "Không xác định";
        Integer quantity = inventory != null ? inventory.getQuantity() : 0;
        return new ProductDetailResponse(
                product.getProductCode(),
                product.getName(),
                product.getUnit(),
               
                product.getSupplier() != null ? product.getSupplier().getName() : "Không có",
                locationName,
                quantity,
                product.getQrCode());
    }

    public ResultPaginationDTO getAllProducts(Pageable pageable) {
        // Lấy danh sách sản phẩm phân trang
        Page<Product> productPage = productRepository.findAll(pageable);

        // Ánh xạ danh sách sản phẩm sang ProductListResponse, bao gồm thông tin tồn kho
        List<ProductListResponse> productList = productPage.getContent().stream()
                .map(product -> {
                    // Gọi getProductDetailByCode để lấy thông tin tồn kho
                    ProductDetailResponse detail = getProductDetailByCode(product.getProductCode());
                    return new ProductListResponse(
                            product.getId(),
                            product.getProductCode(),
                            product.getName(),
                            product.getUnit(),
                            product.getSupplier().getId(),
                            product.getSupplier() != null ? product.getSupplier().getName() : "Không có",
                            product.getMinStock(),
                            product.getExpirationDate(),
                            product.getCreatedAt(),
                            detail.getQuantity() // Lấy số lượng tồn kho từ ProductDetailResponse
                    );
                })
                .collect(Collectors.toList());

        // Tạo ResultPaginationDTO để trả về
        ResultPaginationDTO response = new ResultPaginationDTO();
        response.setContent(productList);
        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());

        return response;
    }

    @Transactional
    public Product updateMinStock(String productCode, Integer minStock) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        product.setMinStock(minStock);
        return productRepository.save(product);
    }

    @Transactional
    public void updateBatchMinStock(List<Map<String, Object>> updates) {
        for (Map<String, Object> update : updates) {
            String productCode = (String) update.get("productCode");
            Integer minStock = (Integer) update.get("minStock");
            if (productCode == null || minStock == null) {
                throw new RuntimeException("Thiếu productCode hoặc minStock trong yêu cầu cập nhật");
            }
            Product product = productRepository.findByProductCode(productCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
            product.setMinStock(minStock);
            productRepository.save(product);
        }
    }

    public String getQrCodeByProductCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với mã: " + productCode));
        return product.getQrCode();
    }
}