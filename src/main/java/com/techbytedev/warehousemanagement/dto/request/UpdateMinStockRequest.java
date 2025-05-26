package com.techbytedev.warehousemanagement.dto.request;

import jakarta.validation.constraints.NotNull;

public class UpdateMinStockRequest {
    @NotNull(message = "minStock không được để trống")
private Integer minStock;

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }
}
