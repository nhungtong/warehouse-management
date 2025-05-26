package com.techbytedev.warehousemanagement.dto.response;

import com.techbytedev.warehousemanagement.entity.StockCheck;
import lombok.Data;

@Data
public class StockCheckResponse {
    private String productCode;
    private String productName;
    private String locationName;
    private String unit;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private String status;
    private Integer difference;

    public StockCheckResponse(StockCheck stockCheck) {
        this.productCode = stockCheck.getProduct().getProductCode();
        this.productName = stockCheck.getProduct().getName();
        this.locationName = stockCheck.getLocation().getName();
        this.unit = stockCheck.getProduct().getUnit();
        this.systemQuantity = stockCheck.getSystemQuantity();
        this.actualQuantity = stockCheck.getActualQuantity();
        this.difference = actualQuantity - systemQuantity;
        this.status = (difference == 0) ? "Trùng khớp" : "Phát hiện sai lệch";
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(Integer systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDifference() {
        return difference;
    }

    public void setDifference(Integer difference) {
        this.difference = difference;
    }
}
