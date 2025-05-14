package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.service.StockCheckService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
}
