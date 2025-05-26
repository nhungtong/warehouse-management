package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {
    @Query("SELECT s.productCode, s.quantity FROM InventorySnapshot s WHERE s.snapshotDate = (SELECT MAX(snapshotDate) FROM InventorySnapshot WHERE snapshotDate <= :date)")
    List<Object[]> findLatestSnapshotBeforeDate(LocalDateTime date);
}