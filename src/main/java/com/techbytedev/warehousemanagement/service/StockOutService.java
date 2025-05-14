package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.repository.StockOutDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class StockOutService {
    private final StockOutDetailRepository stockOutDetailRepository;

    public StockOutService(StockOutDetailRepository stockOutDetailRepository) {
        this.stockOutDetailRepository = stockOutDetailRepository;
    }

    public long getTotalStockOut() {
        return stockOutDetailRepository.getTotalQuantity();
    }
}
