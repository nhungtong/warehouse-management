package com.techbytedev.warehousemanagement.dto.response;
import lombok.Data;
@Data
public class ProductDetailResponse {
    private String productCode;
    private String productName;
    private String unit;
    private String supplierName;
    private String locationName;

    public ProductDetailResponse(String productCode, String productName, String unit, String supplierName, String locationName) {
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.supplierName = supplierName;
        this.locationName = locationName;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
