package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockInForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockInFormRepository extends JpaRepository<StockInForm, Integer> {
}
