package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockCheckRepository extends JpaRepository<StockCheck, Integer> {
}
