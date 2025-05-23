package com.techbytedev.warehousemanagement.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockCheckScheduler {
    private final StockCheckService stockCheckService;

    public StockCheckScheduler(StockCheckService stockCheckService) {
        this.stockCheckService = stockCheckService;
    }

    // Lên lịch chạy vào 23:59 ngày cuối cùng của mỗi tháng
    @Scheduled(cron = "0 59 23 L * ?")
    public void scheduleMonthlyStockCheckReport() {
        stockCheckService.sendMonthlyReportToAdmins();
    }
}