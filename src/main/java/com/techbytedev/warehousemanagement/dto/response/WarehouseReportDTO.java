package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

@Data
public class WarehouseReportDTO {
    private String productCode; // Mã hàng
    private String productName; // Tên hàng
    private Integer openingStock; // Số lượng đầu kỳ
    private Integer totalIn; // Tổng nhập trong kỳ
    private Integer totalOut; // Tổng xuất trong kỳ
    private Integer closingStock; // Tồn kho cuối kỳ
}