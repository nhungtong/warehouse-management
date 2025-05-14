package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.service.StockInService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stockins")
public class StockInController {
    private final StockInService stockInService;

    public StockInController(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @GetMapping("/count")
    public long count() {
        return stockInService.getTotalStockIn();
    }
}
