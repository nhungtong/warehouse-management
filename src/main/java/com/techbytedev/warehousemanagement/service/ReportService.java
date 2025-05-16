package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.ProductReportDTO;
import com.techbytedev.warehousemanagement.dto.response.WarehouseReportDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.ProductRepository;
import com.techbytedev.warehousemanagement.repository.StockInDetailRepository;
import com.techbytedev.warehousemanagement.repository.StockOutDetailRepository;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ReportService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private StockInDetailRepository stockInDetailRepository;

    @Autowired
    private StockOutDetailRepository stockOutDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    
public List<ProductReportDTO> getProductReport(String filterType) {
        LocalDateTime now = LocalDateTime.now(); // 11:16 AM, 16/05/2025
        LocalDateTime oneMonthAgo = now.minusMonths(1); // 16/04/2025 11:16 AM
        LocalDateTime tenDaysFromNow = now.plusDays(10); // 26/05/2025 11:16 AM

        List<Product> products = productRepository.findAll();
        List<ProductReportDTO> report = new ArrayList<>();

        // Lấy ngày xuất kho gần nhất cho từng sản phẩm
        List<Object[]> lastOutDates = stockOutDetailRepository.findLastOutDateByProduct();
        Map<String, LocalDateTime> lastOutDateMap = lastOutDates.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (LocalDateTime) result[1]
                ));

        // Lấy danh sách sản phẩm có xuất kho trong 1 tháng qua
        List<String> activeProductCodes = stockOutDetailRepository.findProductsWithOutInLastMonth(oneMonthAgo);

        for (Product product : products) {
            // Tính tổng tồn kho hiện tại
            Integer currentStock = inventoryRepository.findByProductCode(product.getProductCode()).stream()
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            ProductReportDTO dto = new ProductReportDTO();
            dto.setProductCode(product.getProductCode());
            dto.setExpirationDate(product.getExpirationDate() != null ? new java.sql.Date(product.getExpirationDate().getTime()) : null);
            dto.setLastOutDate(lastOutDateMap.getOrDefault(product.getProductCode(), null));
            dto.setCurrentStock(currentStock);

            boolean isNearExpiration = product.getExpirationDate() != null &&
                    product.getExpirationDate().before(new Date(tenDaysFromNow.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())) &&
                    product.getExpirationDate().after(new Date(now.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
            boolean isSlowMoving = !activeProductCodes.contains(product.getProductCode());

            if ("near_expiration".equals(filterType) && isNearExpiration) {
                report.add(dto);
            } else if ("slow_moving".equals(filterType) && isSlowMoving) {
                report.add(dto);
            } else if (filterType == null) {
                report.add(dto); // Trả về tất cả nếu không có bộ lọc
            }
        }

        return report;
    }

public byte[] exportProductReportToExcel(String filterType) throws IOException {
        List<ProductReportDTO> report = getProductReport(filterType);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo sản phẩm");

        // Tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã hàng", "Ngày hết hạn", "Ngày xuất kho gần nhất", "Số lượng tồn hiện tại"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Dữ liệu
        int rowNum = 1;
        for (ProductReportDTO dto : report) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getProductCode());
            if (dto.getExpirationDate() != null) {
                row.createCell(1).setCellValue(dto.getExpirationDate().toString());
            }
            if (dto.getLastOutDate() != null) {
                row.createCell(2).setCellValue(dto.getLastOutDate().toString());
            }
            row.createCell(3).setCellValue(dto.getCurrentStock() != null ? dto.getCurrentStock() : 0);
        }

        // Xuất file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public List<WarehouseReportDTO> getWarehouseReport(LocalDateTime startDate, LocalDateTime endDate) {
        

        // Lấy số lượng đầu kỳ
        List<Object[]> openingStockResults = inventoryRepository.getOpeningStock(startDate);
        Map<String, Integer> openingStockMap = new HashMap<>();
        for (Object[] result : openingStockResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            openingStockMap.put(productCode, quantity != null ? quantity : 0);
        }

        // Lấy tổng nhập trong kỳ
        List<Object[]> totalInResults = stockInDetailRepository.getTotalIn(startDate, endDate);
        Map<String, Integer> totalInMap = new HashMap<>();
        for (Object[] result : totalInResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            totalInMap.put(productCode, quantity != null ? quantity : 0);
        }

        // Lấy tổng xuất trong kỳ
        List<Object[]> totalOutResults = stockOutDetailRepository.getTotalOut(startDate, endDate);
        Map<String, Integer> totalOutMap = new HashMap<>();
        for (Object[] result : totalOutResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            totalOutMap.put(productCode, quantity != null ? quantity : 0);
        }

        // Tạo báo cáo
        List<WarehouseReportDTO> report = new ArrayList<>();
        // Lấy danh sách mã hàng từ các bản ghi (có thể mở rộng để lấy từ bảng products)
        Set<String> allProductCodes = new HashSet<>();
        allProductCodes.addAll(openingStockMap.keySet());
        allProductCodes.addAll(totalInMap.keySet());
        allProductCodes.addAll(totalOutMap.keySet());

        for (String productCode : allProductCodes) {
            WarehouseReportDTO dto = new WarehouseReportDTO();
            dto.setProductCode(productCode);
            dto.setOpeningStock(openingStockMap.getOrDefault(productCode, 0));
            dto.setTotalIn(totalInMap.getOrDefault(productCode, 0));
            dto.setTotalOut(totalOutMap.getOrDefault(productCode, 0));
            dto.setClosingStock(dto.getOpeningStock() + dto.getTotalIn() - dto.getTotalOut());
            report.add(dto);
        }

        return report;
    }
}