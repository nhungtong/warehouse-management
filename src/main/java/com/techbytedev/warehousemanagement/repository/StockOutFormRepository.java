package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.StockOutForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutFormRepository extends JpaRepository<StockOutForm, Integer> {
}
