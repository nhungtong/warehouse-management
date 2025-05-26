package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
public class ProductReportDTO {
    private String productCode;
    private String productName; // Thêm trường productName
    private Date expirationDate;
    private LocalDateTime lastOutDate;
    private Integer currentStock;
}