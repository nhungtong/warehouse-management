package com.techbytedev.warehousemanagement.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class StockInRequest {
    private String ProductCode;
    private String ProductName;
    private Integer quantity;
    private String unit;
    private String supplierName;
    private String locationName;
    private BigDecimal unitPrice;
    private MultipartFile invoiceFile;
    private String username;
}
