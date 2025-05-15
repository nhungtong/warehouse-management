package com.techbytedev.warehousemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutDetailRepository extends JpaRepository<StockOutDetailRepository, Integer> {
}
