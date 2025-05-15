package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StockCheckRepository extends JpaRepository<StockCheck, Integer> {
    @Query("SELECT MAX(s.createAt) FROM StockCheck s")
    LocalDateTime getMaxCreatedAt();
}