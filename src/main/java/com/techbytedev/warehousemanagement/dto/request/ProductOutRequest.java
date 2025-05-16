package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;

@Data
public class ProductOutRequest {
    private String productCode;
    private Integer quantity;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
