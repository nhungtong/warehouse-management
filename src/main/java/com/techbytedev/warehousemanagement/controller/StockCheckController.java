package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.StockCheckRequest;
import com.techbytedev.warehousemanagement.dto.response.StockCheckResponse;
import com.techbytedev.warehousemanagement.entity.User;
import com.techbytedev.warehousemanagement.service.StockCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

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
    public LocalDateTime getLate() {
        return stockCheckService.getMaxCreateAt();
    }
    // Thực hiện kiểm kê
    @PostMapping("/perform")
    public ResponseEntity<StockCheckResponse> performStockCheck(
            @RequestBody StockCheckRequest stockCheckRequest,
            @RequestParam String username) {
        try {
            StockCheckResponse result = stockCheckService.performStockCheck(stockCheckRequest, username);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // Lấy danh sách kiểm kê trong ngày
    @GetMapping("/today")
    public ResponseEntity<List<StockCheckResponse>> getTodayStockChecks() {
        List<StockCheckResponse> result = stockCheckService.getTodayStockChecks();
        return ResponseEntity.ok(result);
    }
}
