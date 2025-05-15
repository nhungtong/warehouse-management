package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.StockOutRequest;
import com.techbytedev.warehousemanagement.service.StockOutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-out")
public class StockOutController {
    private final StockOutService stockOutService;

    public StockOutController(StockOutService stockOutService) {
        this.stockOutService = stockOutService;
    }
    @GetMapping("/count")
    public long count() {
        return stockOutService.getTotalStockOut();
    }
    @PostMapping()
    public ResponseEntity<?> handleStockOut(
            @RequestBody StockOutRequest request
    ) {
        try {
            stockOutService.handleStockOut(request);
            return ResponseEntity.ok("Xuất kho thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
