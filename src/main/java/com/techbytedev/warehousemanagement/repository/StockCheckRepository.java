package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.entity.StockCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockCheckRepository extends JpaRepository<StockCheck, Integer> {
    @Query("SELECT MAX(s.createAt) FROM StockCheck s")
    LocalDateTime getMaxCreatedAt();

    @Query("SELECT sc FROM StockCheck sc WHERE sc.product.productCode = :productCode")
    List<StockCheck> findByProductCode(@Param("productCode") String productCode);

    @Query("SELECT s FROM StockCheck s WHERE DATE(s.createAt) = CURRENT_DATE")
    List<StockCheck> findAllByToday();

}
