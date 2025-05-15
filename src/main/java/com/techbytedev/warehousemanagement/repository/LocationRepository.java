package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findAll();
    Optional<Location> findByName(String name);
}
