package com.techbytedev.warehousemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.warehousemanagement.dto.request.StockInRequest;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDTO;
import com.techbytedev.warehousemanagement.dto.response.StockInFormDetailViewDTO;
import com.techbytedev.warehousemanagement.service.StockInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @GetMapping("/forms")
    public ResponseEntity<List<StockInFormDTO>> getAllForms() {
        List<StockInFormDTO> forms = stockInService.getAllForms();
        return ResponseEntity.ok(forms);
    }
    // lấy chi tiết phiếu nhập
    @GetMapping("/forms/{id}")
    public ResponseEntity<StockInFormDetailViewDTO> getFormDetails(@PathVariable Integer id) {
        StockInFormDetailViewDTO formDetails = stockInService.getFormWithDetails(id);
        return ResponseEntity.ok(formDetails);
    }
}
