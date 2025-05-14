package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockOutDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutDetailRepository extends JpaRepository<StockOutDetail, Integer> {
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockOutDetail s")
    Long getTotalQuantity();
}
