package com.techbytedev.warehousemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbytedev.warehousemanagement.dto.request.StockInRequest;
import com.techbytedev.warehousemanagement.service.StockInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/stock-in")
public class StockInController {
    private final StockInService stockInService;

    public StockInController(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @GetMapping("/count")
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
}
