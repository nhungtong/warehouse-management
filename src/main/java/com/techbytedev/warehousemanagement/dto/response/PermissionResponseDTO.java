package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

@Data
public class PermissionResponseDTO {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
}