package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByProductCode(String productCode);
    Optional<Product> findByName(String name);
    Optional<Product> findByUnit(String unit);

    @Query("SELECT p FROM Product p WHERE p.expirationDate BETWEEN :startDate AND :endDate")
    List<Product> findByExpirationDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Product p LEFT JOIN StockOutDetail sod ON sod.product.id = p.id " +
           "LEFT JOIN StockOutForm sof ON sof.id = sod.stockOutForm.id " +
           "WHERE sof.createdAt IS NULL OR sof.createdAt < :oneMonthAgo " +
           "GROUP BY p.id")
    List<Product> findSlowMovingProducts(@Param("oneMonthAgo") LocalDateTime oneMonthAgo);
}