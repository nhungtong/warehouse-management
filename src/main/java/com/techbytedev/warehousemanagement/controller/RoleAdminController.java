package com.techbytedev.warehousemanagement.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techbytedev.warehousemanagement.dto.request.RoleCreateDTO;
import com.techbytedev.warehousemanagement.dto.request.RoleFilterDTO;
import com.techbytedev.warehousemanagement.dto.request.RoleUpdateDTO;
import com.techbytedev.warehousemanagement.dto.response.ResultPaginationDTO;
import com.techbytedev.warehousemanagement.dto.response.RoleResponseDTO;
import com.techbytedev.warehousemanagement.entity.Role;
import com.techbytedev.warehousemanagement.service.RoleService;

import java.util.ArrayList;
import java.util.Optional;
import jakarta.persistence.criteria.Predicate;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleAdminController {

    private final RoleService roleService;

    public RoleAdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            RoleFilterDTO filter,
            Pageable pageable
    ) {
        Specification<Role> spec = buildSpecification(filter);
        ResultPaginationDTO result = roleService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(@RequestBody RoleCreateDTO request) {
        RoleResponseDTO response = roleService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody RoleUpdateDTO request
    ) {
        RoleResponseDTO response = roleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
@GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> fetchById(@PathVariable Integer id) {
        RoleResponseDTO response = roleService.fetchById(id);
        return ResponseEntity.ok(response);
    }
    private Specification<Role> buildSpecification(RoleFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return null;
            }

            var predicates = Optional.ofNullable(filter)
                    .map(f -> {
                        var p = new ArrayList<Predicate>();
                        if (f.getName() != null && !f.getName().isEmpty()) {
                            p.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + f.getName().toLowerCase() + "%"));
                        }
                        if (f.getActive() != null) {
                            p.add(criteriaBuilder.equal(root.get("active"), f.getActive()));
                        }
                        if (f.getDescription() != null && !f.getDescription().isEmpty()) {
                            p.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + f.getDescription().toLowerCase() + "%"));
                        }
                        return p;
                    })
                    .orElse(new ArrayList<Predicate>());

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}