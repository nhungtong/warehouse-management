package com.techbytedev.warehousemanagement.dto.response;
import lombok.Data;

@Data
public class DemandForecastDTO {
    private String productCode; // Mã hàng
    private String productName; // Thêm trường productName
    private Integer forecastQuantity; // Số lượng dự báo tuần tới
    private Integer currentStock; // Tồn kho hiện tại
    private Integer suggestedIn; // Lượng nhập gợi ý
}