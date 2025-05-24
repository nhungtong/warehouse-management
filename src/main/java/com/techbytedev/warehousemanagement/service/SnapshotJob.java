package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.entity.InventorySnapshot;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.InventorySnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SnapshotJob {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventorySnapshotRepository inventorySnapshotRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Chạy vào 00:00 ngày 1 mỗi tháng
    public void createMonthlySnapshot() {
        LocalDateTime snapshotDate = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59);
        List<Object[]> currentStocks = inventoryRepository.getOpeningStock(snapshotDate);

        for (Object[] result : currentStocks) {
            InventorySnapshot snapshot = new InventorySnapshot();
            snapshot.setProductCode((String) result[0]);
            snapshot.setQuantity(((Number) result[1]).intValue());
            snapshot.setSnapshotDate(snapshotDate);
            inventorySnapshotRepository.save(snapshot);
        }
    }
}