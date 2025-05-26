package com.techbytedev.warehousemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techbytedev.warehousemanagement.entity.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Optional<Role> findById(Integer id);

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    @Query("SELECT r FROM Role r WHERE r.active = true ORDER BY r.id")
    List<Role> findAllWithPermissions();

    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Optional<Role> findByIdWithPermissions(@Param("id") Integer id);
}