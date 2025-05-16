package com.techbytedev.warehousemanagement.dto.response;

import com.techbytedev.warehousemanagement.entity.StockCheck;
import lombok.Data;

@Data
public class StockCheckResponse {
    private String productCode;
    private String productName;
    private String locationName;
    private Integer systemQuantity;
    private Integer actualQuantity;
    private String status;
    private Integer difference;

    public StockCheckResponse(StockCheck stockCheck) {
        this.productCode = stockCheck.getProduct().getProductCode();
        this.productName = stockCheck.getProduct().getName();
        this.locationName = stockCheck.getLocation().getName();
        this.systemQuantity = stockCheck.getSystemQuantity();
        this.actualQuantity = stockCheck.getActualQuantity();
        this.difference = actualQuantity - systemQuantity;
        this.status = (difference == 0) ? "Trùng khớp" : "Phát hiện sai lệch";
    }
}
