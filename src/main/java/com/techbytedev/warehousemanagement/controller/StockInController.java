package com.techbytedev.warehousemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.warehousemanagement.dto.request.StockInRequest;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDTO;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDetailViewDTO;
import com.techbytedev.warehousemanagement.service.StockInService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-in")
public class StockInController {
    private final StockInService stockInService;

    public StockInController(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @GetMapping("/count")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stock-in/**', 'GET')")
    public long count() {
        return stockInService.getTotalStockIn();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> handleStockIn(
            @RequestPart("data") String data,
            @RequestPart(value = "invoiceFile", required = false) MultipartFile invoiceFile) {
        try {
            System.out.println("Received JSON data: " + data);
            StockInRequest requestDTO = new ObjectMapper().readValue(data, StockInRequest.class);
            StockInRequest result = stockInService.handleStockIn(requestDTO, invoiceFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
    // lấy danh sách phiếu nhập hàng
    @GetMapping("/stockin/forms")
    public ResponseEntity<Map<String, Object>> getStockInForms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<StockInFormDTO> formPage = stockInService.getAllForms(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", formPage.getContent());
        response.put("pageNumber", formPage.getNumber() + 1);
        response.put("pageSize", formPage.getSize());
        response.put("totalPages", formPage.getTotalPages());
        response.put("totalElements", formPage.getTotalElements());
        response.put("last", formPage.isLast());

        return ResponseEntity.ok(response);
    }

    // lấy chi tiết phiếu nhập
    @GetMapping("/forms/{id}")
    public ResponseEntity<StockInFormDetailViewDTO> getFormDetails(@PathVariable Integer id) {
        StockInFormDetailViewDTO formDetails = stockInService.getFormWithDetails(id);
        return ResponseEntity.ok(formDetails);
    }
}
