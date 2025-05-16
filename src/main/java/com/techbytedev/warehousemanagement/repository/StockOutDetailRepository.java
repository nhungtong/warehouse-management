package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockOutDetail;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutDetailRepository extends JpaRepository<StockOutDetail, Integer> {
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM StockOutDetail s")
    Long getTotalQuantity();
@Query("SELECT s.product.productCode, SUM(s.quantity) " +
           "FROM StockOutDetail s JOIN s.stockOutForm f " +
           "WHERE f.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.product.productCode")
    List<Object[]> getTotalOut(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s.product.productCode, SUM(s.quantity) " +
           "FROM StockOutDetail s JOIN s.stockOutForm f " +
           "WHERE f.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.product.productCode")
    List<Object[]> getWeeklyOutByProduct(LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT MAX(sof.createdAt) FROM StockOutDetail sod " +
           "JOIN sod.stockOutForm sof " +
           "WHERE sod.product.productCode = :productCode")
    LocalDateTime findLastOutDateByProductCode(@Param("productCode") String productCode);
    @Query("SELECT s.product.productCode, MAX(f.createdAt) AS lastOutDate " +
           "FROM StockOutDetail s " +
           "JOIN s.stockOutForm f " +
           "GROUP BY s.product.productCode")
    List<Object[]> findLastOutDateByProduct();

    @Query("SELECT s.product.productCode " +
           "FROM StockOutDetail s " +
           "JOIN s.stockOutForm f " +
           "WHERE f.createdAt >= :startDate " +
           "GROUP BY s.product.productCode")
    List<String> findProductsWithOutInLastMonth(@Param("startDate") LocalDateTime startDate);
}


