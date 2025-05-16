package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockInDetail;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockInDetailRepository extends JpaRepository<StockInDetail, Integer> {
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockInDetail s")
    Long getTotalQuantity();
    @Query("SELECT s.product.productCode, SUM(s.quantity) " +
           "FROM StockInDetail s JOIN s.stockInForm f " +
           "WHERE f.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.product.productCode")
    List<Object[]> getTotalIn(LocalDateTime startDate, LocalDateTime endDate);
}

