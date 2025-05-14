package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockOutDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutDetailRepository extends JpaRepository<StockOutDetail, Integer> {
}
