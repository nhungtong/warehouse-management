package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.StockCheckRequest;
import com.techbytedev.warehousemanagement.dto.response.StockCheckResponse;
import com.techbytedev.warehousemanagement.service.StockCheckService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stockcheck")
public class StockCheckController {
    private final StockCheckService stockCheckService;

    public StockCheckController(StockCheckService stockCheckService) {
        this.stockCheckService = stockCheckService;
    }
    @GetMapping("/late")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stockcheck/**', 'GET')")
    public LocalDateTime getLate() {
        return stockCheckService.getMaxCreateAt();
    }
    // Thực hiện kiểm kê
    @PostMapping("/perform")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stockcheck/**', 'POST')")
    public ResponseEntity<List<StockCheckResponse>> performStockCheck(
            @RequestBody List<StockCheckRequest> stockCheckRequests,
            @RequestParam String username) {
        try {
            List<StockCheckResponse> results = stockCheckService.performStockCheck(stockCheckRequests, username);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // Lấy danh sách kiểm kê trong thang
    @GetMapping("/month")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stockcheck/**', 'GET')")
    public ResponseEntity<List<StockCheckResponse>> getMonthStockChecks() {
        List<StockCheckResponse> result = stockCheckService.getMonthStockChecks();
        return ResponseEntity.ok(result);
    }
    // Lấy danh sách kiểm kê theo ngày
    @GetMapping("/by-date")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stockcheck/**', 'GET')")
    public ResponseEntity<List<StockCheckResponse>> getStockChecksByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<StockCheckResponse> responses = stockCheckService.getStockCheckResponsesByDate(date);
        return ResponseEntity.ok(responses);
    }
    // xuất excel theo ngày
    @GetMapping("/export-excel")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stockcheck/**', 'GET')")
    public ResponseEntity<InputStreamResource> exportStockCheckExcel(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {

        ByteArrayInputStream in = stockCheckService.exportStockCheckExcelByDate(date);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=stock_check_" + date + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}