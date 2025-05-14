package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    long countById(Integer id);
}
