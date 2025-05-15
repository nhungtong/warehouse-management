package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class RoleUpdateDTO {
    private String name;
    private String description;
    private Boolean active;
}