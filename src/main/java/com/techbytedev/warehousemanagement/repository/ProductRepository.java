package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    long count();
    List<Product> findAll();
    Optional<Product> findByProductCode(String productCode);
    Optional<Product> findByProductName(String productName);
    Optional<Product> findByUnit(String unit);
}
