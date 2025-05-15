package com.techbytedev.warehousemanagement.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techbytedev.warehousemanagement.entity.RolePermission;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}