package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductInRequest;
import com.techbytedev.warehousemanagement.dto.request.StockInRequest;
import com.techbytedev.warehousemanagement.dto.response.StockInDetailDTO;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDTO;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDetailViewDTO;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;
import com.techbytedev.warehousemanagement.util.QRCodeGeneratorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockInService {
    private final StockInDetailRepository stockInDetailRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final StockInFormRepository stockInFormRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SettingService settingService;

    public StockInService(StockInDetailRepository stockInDetailRepository, ProductRepository productRepository,
                          SupplierRepository supplierRepository, LocationRepository locationRepository,
                          StockInFormRepository stockInFormRepository, InventoryRepository inventoryRepository,
                          UserRepository userRepository, EmailService emailService, SettingService settingService) {
        this.stockInDetailRepository = stockInDetailRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.locationRepository = locationRepository;
        this.stockInFormRepository = stockInFormRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.settingService = settingService;
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
                            savedProduct.setQrCode(qrPath);
                             productRepository.save(savedProduct); // Lưu lại với qrCode
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

            // Kiểm tra tồn kho thấp
            if (settingService.isLowStockAlertEnabled() && inventory.getQuantity() <= product.getMinStock()) {
                String[] emails = settingService.getLowStockAlertEmails().split(",");
                emailService.sendLowStockAlert(emails, product.getName(), inventory.getQuantity());
            }
        }

        return requestDTO;
    }
    private StockInFormDTO toDTO(StockInForm form) {
        StockInFormDTO dto = new StockInFormDTO();
        dto.setId(form.getId());
        dto.setCode(form.getCode());
        dto.setNote(form.getNote());
        dto.setCreatedAt(form.getCreatedAt());
        dto.setUsername(form.getCreatedBy().getUsername());
        dto.setInvoiceFile(form.getInvoiceFile());
        return dto;
    }
    private StockInDetailDTO toDetailDTO(StockInDetail detail) {
        StockInDetailDTO dto = new StockInDetailDTO();
        dto.setProductName(detail.getProduct().getName());
        dto.setSupplierName(detail.getProduct().getSupplier().getName());
        dto.setQuantity(detail.getQuantity());
        dto.setUnit(detail.getProduct().getUnit());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setLocationName(detail.getLocation().getName());
        return dto;
    }
    public Page<StockInFormDTO> getAllForms(Pageable pageable) {
        return stockInFormRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public StockInFormDetailViewDTO getFormWithDetails(Integer id) {
        StockInForm form = stockInFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu nhập không tồn tại"));

        StockInFormDetailViewDTO dto = new StockInFormDetailViewDTO();
        dto.setId(form.getId());
        dto.setCode(form.getCode());
        dto.setNote(form.getNote());
        dto.setCreatedAt(form.getCreatedAt());
        dto.setUsername(form.getCreatedBy().getUsername());
        dto.setInvoiceFile(form.getInvoiceFile());

        List<StockInDetailDTO> details = form.getStockInDetails()
                .stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());

        dto.setDetails(details);

        return dto;
    }
}
