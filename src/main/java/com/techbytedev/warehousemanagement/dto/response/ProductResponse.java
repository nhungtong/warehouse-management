package com.techbytedev.warehousemanagement.dto.response;

import com.techbytedev.warehousemanagement.entity.Product;
import lombok.Data;

@Data
public class ProductResponse {
    private String productCode;
    private String name;
    private String unit;
    private String supplierName;
    private String locationName;
    private Integer quantity;

    public ProductResponse(Product product, Integer quantity) {
        this.productCode = productCode;
        this.name = name;
        this.unit = unit;
        this.supplierName = supplierName;
        this.locationName = locationName;
        this.quantity = quantity;
    }
}
