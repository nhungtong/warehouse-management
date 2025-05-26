package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;

@Data
public class StockCheckRequest {

    private String productCode;
    private Integer actualQuantity;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }
}
