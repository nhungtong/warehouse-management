package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.repository.StockCheckRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StockCheckService {
    private StockCheckRepository stockCheckRepository;

    public StockCheckService(StockCheckRepository stockCheckRepository) {
        this.stockCheckRepository = stockCheckRepository;
    }
    public LocalDateTime getMaxCreateAt()
    {
        return stockCheckRepository.getMaxCreatedAt();
    }
}
