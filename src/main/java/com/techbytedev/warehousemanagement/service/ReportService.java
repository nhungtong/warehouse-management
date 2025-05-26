package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.ProductReportDTO;
import com.techbytedev.warehousemanagement.dto.response.WarehouseReportDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.InventorySnapshotRepository;
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

    @Autowired
    private InventorySnapshotRepository inventorySnapshotRepository;

    public List<ProductReportDTO> getProductReport(String filterType) {
        LocalDateTime now = LocalDateTime.now(); // 04:34 PM, 24/05/2025
        LocalDateTime oneMonthAgo = now.minusMonths(1); // 24/04/2025 04:34 PM
        LocalDateTime tenDaysFromNow = now.plusDays(10); // 03/06/2025 04:34 PM

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
            dto.setProductName(product.getName() != null ? product.getName() : "Không có tên"); // Thêm tên sản phẩm
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
        String[] headers = {"Mã hàng", "Tên Sản Phẩm", "Ngày hết hạn", "Ngày xuất kho gần nhất", "Số lượng tồn hiện tại"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Dữ liệu
        int rowNum = 1;
        for (ProductReportDTO dto : report) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getProductCode());
            row.createCell(1).setCellValue(dto.getProductName() != null ? dto.getProductName() : "Không có tên"); // Thêm cột tên sản phẩm
            if (dto.getExpirationDate() != null) {
                row.createCell(2).setCellValue(dto.getExpirationDate().toString());
            } else {
                row.createCell(2).setCellValue("");
            }
            if (dto.getLastOutDate() != null) {
                row.createCell(3).setCellValue(dto.getLastOutDate().toString());
            } else {
                row.createCell(3).setCellValue("");
            }
            row.createCell(4).setCellValue(dto.getCurrentStock() != null ? dto.getCurrentStock() : 0);
        }

        // Xuất file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public List<WarehouseReportDTO> getWarehouseReport(LocalDateTime startDate, LocalDateTime endDate) {
        // Tìm mốc cố định gần nhất (cuối tháng trước startDate)
        LocalDateTime snapshotDate = startDate.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        snapshotDate = snapshotDate.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        // Lấy số lượng tồn kho từ snapshot
        List<Object[]> snapshotResults = inventorySnapshotRepository.findLatestSnapshotBeforeDate(startDate);
        Map<String, Integer> openingStockMap = new HashMap<>();
        for (Object[] result : snapshotResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            openingStockMap.put(productCode, quantity != null ? quantity : 0);
        }

        // Điều chỉnh số lượng đầu kỳ dựa trên giao dịch từ snapshotDate đến startDate
        List<Object[]> inAdjustments = stockInDetailRepository.getTotalIn(snapshotDate, startDate);
        for (Object[] result : inAdjustments) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            openingStockMap.merge(productCode, quantity, Integer::sum);
        }

        List<Object[]> outAdjustments = stockOutDetailRepository.getTotalOut(snapshotDate, startDate);
        for (Object[] result : outAdjustments) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            openingStockMap.merge(productCode, -quantity, Integer::sum);
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

        // Lấy tất cả sản phẩm để ánh xạ productCode với productName
        List<Product> products = productRepository.findAll();
        Map<String, String> productNameMap = products.stream()
                .collect(Collectors.toMap(Product::getProductCode, product -> product.getName() != null ? product.getName() : "Không có tên"));

        // Tạo báo cáo
        List<WarehouseReportDTO> report = new ArrayList<>();
        Set<String> allProductCodes = new HashSet<>();
        allProductCodes.addAll(openingStockMap.keySet());
        allProductCodes.addAll(totalInMap.keySet());
        allProductCodes.addAll(totalOutMap.keySet());

        for (String productCode : allProductCodes) {
            WarehouseReportDTO dto = new WarehouseReportDTO();
            dto.setProductCode(productCode);
            dto.setProductName(productNameMap.getOrDefault(productCode, "Không có tên")); // Thêm tên sản phẩm
            dto.setOpeningStock(openingStockMap.getOrDefault(productCode, 0));
            dto.setTotalIn(totalInMap.getOrDefault(productCode, 0));
            dto.setTotalOut(totalOutMap.getOrDefault(productCode, 0));
            dto.setClosingStock(dto.getOpeningStock() + dto.getTotalIn() - dto.getTotalOut());
            report.add(dto);
        }

        return report;
    }
}