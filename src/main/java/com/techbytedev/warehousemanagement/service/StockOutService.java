package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductOutRequest;
import com.techbytedev.warehousemanagement.dto.request.StockOutRequest;
import com.techbytedev.warehousemanagement.dto.response.StockOutDetailDTO;
import com.techbytedev.warehousemanagement.dto.response.StockOutFormDTO;
import com.techbytedev.warehousemanagement.dto.response.StockOutFormDetailViewDTO;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockOutService {
    private final StockOutDetailRepository stockOutDetailRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockOutFormRepository stockOutFormRepository;
    private final InventoryRepository inventoryRepository;
    private final EmailService emailService;
    private final SettingService settingService;

    public StockOutService(StockOutDetailRepository stockOutDetailRepository, UserRepository userRepository,
                           ProductRepository productRepository, StockOutFormRepository stockOutFormRepository,
                           InventoryRepository inventoryRepository, EmailService emailService,
                           SettingService settingService) {
        this.stockOutDetailRepository = stockOutDetailRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.stockOutFormRepository = stockOutFormRepository;
        this.inventoryRepository = inventoryRepository;
        this.emailService = emailService;
        this.settingService = settingService;
    }

    public long getTotalStockOut() {
        return stockOutDetailRepository.getTotalQuantity();
    }

    @Transactional
    public void handleStockOut(StockOutRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        StockOutForm stockOutForm = new StockOutForm();
        stockOutForm.setCode(request.getCode());
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

            // Kiểm tra tồn kho thấp
            if (settingService.isLowStockAlertEnabled() && inventory.getQuantity() <= product.getMinStock()) {
                String[] emails = settingService.getLowStockAlertEmails().split(",");
                emailService.sendLowStockAlert(emails, product.getName(), inventory.getQuantity());
            }

            StockOutDetail detail = new StockOutDetail();
            detail.setStockOutForm(stockOutForm);
            detail.setProduct(product);
            detail.setQuantity(p.getQuantity());
            stockOutDetailRepository.save(detail);
        }
    }
    private StockOutFormDTO toDTO(StockOutForm form) {
        StockOutFormDTO dto = new StockOutFormDTO();
        dto.setId(form.getId());
        dto.setCode(form.getCode());
        dto.setDestination(form.getDestination());
        dto.setUsername(form.getCreatedBy().getUsername());
        dto.setCreatedAt(form.getCreatedAt());
        dto.setNote(form.getNote());
        return dto;
    }

    private StockOutDetailDTO toDetailDTO(StockOutDetail detail) {
        StockOutDetailDTO dto = new StockOutDetailDTO();
        dto.setProductCode(detail.getProduct().getProductCode());
        dto.setProductName(detail.getProduct().getName());
        dto.setQuantity(detail.getQuantity());
        dto.setUnit(detail.getProduct().getUnit());
        return dto;
    }
    public Page<StockOutFormDTO> getAllForms(Pageable pageable) {
        return stockOutFormRepository.findAll(pageable)
                .map(this::toDTO);
    }

    public StockOutFormDetailViewDTO getFormWithDetails(Integer id) {
        StockOutForm form = stockOutFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phiếu xuất không tồn tại"));

        StockOutFormDetailViewDTO dto = new StockOutFormDetailViewDTO();
        dto.setId(form.getId());
        dto.setCode(form.getCode());
        dto.setDestination(form.getDestination());
        dto.setUsername(form.getCreatedBy().getUsername());
        dto.setCreatedAt(form.getCreatedAt());
        dto.setNote(form.getNote());

        List<StockOutDetailDTO> details = form.getStockOutDetails()
                .stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
        dto.setDetails(details);

        return dto;
    }
}