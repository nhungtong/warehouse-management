package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockInDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockInDetailRepository extends JpaRepository<StockInDetail, Integer> {
}
