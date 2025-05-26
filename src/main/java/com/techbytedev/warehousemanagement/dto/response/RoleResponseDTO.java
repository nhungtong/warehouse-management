package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RoleResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<PermissionResponseDTO> permissions; // Optional, chỉ điền khi includePermissions = true

}