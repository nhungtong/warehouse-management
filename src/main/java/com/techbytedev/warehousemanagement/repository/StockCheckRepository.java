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

    @Query(value = "SELECT * FROM stock_check s WHERE MONTH(s.create_at) = MONTH(CURRENT_DATE()) AND YEAR(s.create_at) = YEAR(CURRENT_DATE())", nativeQuery = true)
    List<StockCheck> findAllByCurrentMonth();
}
