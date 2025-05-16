package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Location;
import com.techbytedev.warehousemanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i")
    Long getTotalInventory();

    @Query("SELECT i.createdAt AS date, SUM(i.quantity) AS totalQty " +
            "FROM Inventory i " +
            "WHERE FUNCTION('MONTH', i.createdAt) = FUNCTION('MONTH', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', i.createdAt) = FUNCTION('YEAR', CURRENT_DATE) " +
            "GROUP BY i.createdAt " +
            "ORDER BY i.createdAt")
    List<Object[]> getInventoryCurrentMonth();
    Optional<Inventory> findByProductIdAndLocationId(Integer productId, Integer locationId);
    Optional<Inventory> findByProductId(Integer productId);
    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId")
    Integer sumQuantityByProductId(@Param("productId") Integer productId);
    Optional<Inventory> findByProductAndLocation(Product product, Location location);
    Optional<Inventory> findByProduct(Product product);
}

