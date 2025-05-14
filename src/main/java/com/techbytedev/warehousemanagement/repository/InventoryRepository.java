package com.techbytedev.warehousemanagement.repository;

import com.techbytedev.warehousemanagement.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
