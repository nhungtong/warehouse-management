package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.StockCheckRequest;
import com.techbytedev.warehousemanagement.dto.response.StockCheckResponse;
import com.techbytedev.warehousemanagement.entity.*;
import com.techbytedev.warehousemanagement.repository.*;

import jakarta.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockCheckService {
    private final StockCheckRepository stockCheckRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final ExcelExportService  excelExportService;
    private final EmailService emailService;

    public StockCheckService(StockCheckRepository stockCheckRepository, ProductRepository productRepository,
                             InventoryRepository inventoryRepository, LocationRepository locationRepository,
                             UserRepository userRepository, ExcelExportService excelExportService,
                             EmailService emailService) {
        this.stockCheckRepository = stockCheckRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.excelExportService = excelExportService;
        this.emailService = emailService;
    }

    public LocalDateTime getMaxCreateAt() {
        return stockCheckRepository.getMaxCreatedAt();
    }

    public StockCheckResponse performStockCheck(StockCheckRequest dto, String username) {
        User checkedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findByProductCode(dto.getProductCode())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Location location = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"))
                .getLocation();

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        int systemQuantity = inventory.getQuantity();
        int actualQuantity = dto.getActualQuantity();

        StockCheck stockCheck = new StockCheck();
        stockCheck.setProduct(product);
        stockCheck.setLocation(location);
        stockCheck.setSystemQuantity(systemQuantity);
        stockCheck.setActualQuantity(actualQuantity);
        stockCheck.setCheckedBy(checkedBy);
        stockCheck.setCreatedAt(LocalDateTime.now());

        stockCheckRepository.save(stockCheck);

        return new StockCheckResponse(stockCheck);
    }
    public List<StockCheckResponse> performStockCheck(List<StockCheckRequest> dtos, String username) {
        List<StockCheckResponse> responses = new ArrayList<>();
        for (StockCheckRequest dto : dtos) {
            StockCheckResponse response = performStockCheck(dto, username);  // gọi method cũ xử lý từng cái
            responses.add(response);
        }
        return responses;
    }


    public List<StockCheckResponse> getMonthStockChecks() {
        List<StockCheck> monthChecks = stockCheckRepository.findAllByCurrentMonth();
        return monthChecks.stream()
                .map(StockCheckResponse::new)
                .collect(Collectors.toList());
    }

    // Phương thức gửi báo cáo kiểm kê hàng tháng cho admin
    public void sendMonthlyReportToAdmins() {
        // Lấy danh sách kiểm kê trong tháng
        List<StockCheckResponse> stockChecks = getMonthStockChecks();

        // Lấy danh sách người dùng có vai trò Admin
        Page<User> adminUsers = userRepository.searchUsers(null, null, "Admin", true, Pageable.unpaged());
        String[] adminEmails = adminUsers.stream()
                .map(User::getEmail)
                .toArray(String[]::new);

        // Tạo file Excel từ danh sách kiểm kê
        ByteArrayInputStream excelFile = excelExportService.exportStockChecksToExcel(stockChecks);

        try {
            // Gửi email với file Excel đính kèm
            emailService.sendMonthlyStockCheckReport(adminEmails, excelFile);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email báo cáo: " + e.getMessage());
        }
    }
}