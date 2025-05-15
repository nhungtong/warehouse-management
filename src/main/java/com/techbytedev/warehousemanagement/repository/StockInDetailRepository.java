package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockInDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockInDetailRepository extends JpaRepository<StockInDetail, Integer> {
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockInDetail s")
    Long getTotalQuantity();
}