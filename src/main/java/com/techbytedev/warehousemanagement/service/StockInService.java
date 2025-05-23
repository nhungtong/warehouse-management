package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductInRequest;
import com.techbytedev.warehousemanagement.dto.request.StockInRequest;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;
import com.techbytedev.warehousemanagement.util.QRCodeGeneratorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StockInService {
    private final StockInDetailRepository stockInDetailRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final StockInFormRepository stockInFormRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public StockInService(StockInDetailRepository stockInDetailRepository, ProductRepository productRepository, SupplierRepository supplierRepository, LocationRepository locationRepository, StockInFormRepository stockInFormRepository, InventoryRepository inventoryRepository, UserRepository userRepository) {
        this.stockInDetailRepository = stockInDetailRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.locationRepository = locationRepository;
        this.stockInFormRepository = stockInFormRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    public long getTotalStockIn() {
        return stockInDetailRepository.getTotalQuantity();
    }

    private String saveInvoiceFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/hoadon/";
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalFilename;

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file hóa đơn", e);
        }
    }
    @Transactional
    public StockInRequest handleStockIn(StockInRequest requestDTO, MultipartFile invoiceFile) {
        User user = userRepository.findByUsername(requestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String invoiceFilePath = null;
        if (invoiceFile != null && !invoiceFile.isEmpty()) {
            invoiceFilePath = saveInvoiceFile(invoiceFile);
        }

        StockInForm stockInForm = new StockInForm();
        stockInForm.setCode(requestDTO.getCode());
        stockInForm.setCreatedBy(user);
        stockInForm.setCreatedAt(LocalDateTime.now());
        stockInForm.setInvoiceFile(invoiceFilePath);
        stockInForm.setNote(requestDTO.getNote() != null ? requestDTO.getNote() : "Nhập hàng");
        stockInFormRepository.save(stockInForm);

        for (ProductInRequest productRequest : requestDTO.getProducts()) {

            Supplier supplier = supplierRepository.findByName(productRequest.getSupplierName())
                    .orElseGet(() -> {
                        Supplier newSupplier = new Supplier();
                        newSupplier.setName(productRequest.getSupplierName());
                        return supplierRepository.save(newSupplier);
                    });

            Product product = productRepository.findByProductCode(productRequest.getProductCode())
                    .orElseGet(() -> {
                        Product newProduct = new Product();
                        newProduct.setProductCode(productRequest.getProductCode());
                        newProduct.setName(productRequest.getProductName());
                        newProduct.setUnit(productRequest.getUnit());
                        newProduct.setSupplier(supplier);
                        newProduct.setCreatedAt(LocalDateTime.now());
                        Product savedProduct = productRepository.save(newProduct);
                        try {
                            String qrPath = "uploads/qrcode/" + savedProduct.getProductCode() + ".png";
                            QRCodeGeneratorUtil.generateQRCodeFile(savedProduct.getProductCode(), qrPath);
                        } catch (Exception e) {
                            throw new RuntimeException("Lỗi khi tạo mã QR cho sản phẩm: " + savedProduct.getProductCode(), e);
                        }

                        return savedProduct;
                    });

            Location location = locationRepository.findByName(productRequest.getLocationName())
                    .orElseGet(() -> {
                        Location newLocation = new Location();
                        newLocation.setName(productRequest.getLocationName());
                        return locationRepository.save(newLocation);
                    });

            StockInDetail detail = new StockInDetail();
            detail.setStockInForm(stockInForm);
            detail.setProduct(product);
            detail.setQuantity(productRequest.getQuantity());
            detail.setUnitPrice(productRequest.getUnitPrice());
            detail.setLocation(location);
            stockInDetailRepository.save(detail);

            Inventory inventory = inventoryRepository.findByProductIdAndLocationId(product.getId(), location.getId())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setProduct(product);
                        newInventory.setLocation(location);
                        newInventory.setQuantity(0);
                        return newInventory;
                    });
            inventory.setQuantity(inventory.getQuantity() + productRequest.getQuantity());
            inventoryRepository.save(inventory);
        }

        return requestDTO;
    }

}
