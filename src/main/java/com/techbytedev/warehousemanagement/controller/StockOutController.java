package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.service.StockOutService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stockouts")
public class StockOutController {
    private final StockOutService stockOutService;

    public StockOutController(StockOutService stockOutService) {
        this.stockOutService = stockOutService;
    }
    @GetMapping("/count")
    public long count() {
        return stockOutService.getTotalStockOut();
    }
}
