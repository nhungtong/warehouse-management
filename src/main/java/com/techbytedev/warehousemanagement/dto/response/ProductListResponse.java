package com.techbytedev.warehousemanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ProductListResponse {
    private Integer id;
    private String productCode;
    private String name;
    private String unit;
    private Integer supplierId;
    private String supplierName;
    private Integer minStock;
    private Date expirationDate;
    private String qrCode; // Thêm trường qrCode
    private LocalDateTime createdAt;
    private Integer inventory; // Thêm trường inventory

    public ProductListResponse(Integer id, String productCode, String name, String unit, Integer supplierId,
            String supplierName,
            Integer minStock, Date expirationDate, LocalDateTime createdAt, Integer inventory) {
        this.id = id;
        this.productCode = productCode;
        this.name = name;
        this.unit = unit;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.minStock = minStock;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.inventory = inventory;
    }
}