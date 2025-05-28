package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findByName(String name);
    List<Supplier> findAll();
    Optional<Supplier> findById(Integer id);
}
