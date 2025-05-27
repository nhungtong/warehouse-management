package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.ProductReportDTO;
import com.techbytedev.warehousemanagement.dto.response.ResultPaginationDTO;
import com.techbytedev.warehousemanagement.dto.response.WarehouseReportDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.InventorySnapshotRepository;
import com.techbytedev.warehousemanagement.repository.ProductRepository;
import com.techbytedev.warehousemanagement.repository.StockInDetailRepository;
import com.techbytedev.warehousemanagement.repository.StockOutDetailRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public ResultPaginationDTO getProductReport(String filterType, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        LocalDateTime tenDaysFromNow = now.plusDays(10);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
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

        for (Product product : productPage.getContent()) {
            Integer currentStock = inventoryRepository.findByProductCode(product.getProductCode()).stream()
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            ProductReportDTO dto = new ProductReportDTO();
            dto.setProductCode(product.getProductCode());
            dto.setProductName(product.getName() != null ? product.getName() : "Không có tên");
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
            } else if (filterType == null || "all".equals(filterType)) {
                report.add(dto);
            }
        }

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setContent(report);
        result.setPage(productPage.getNumber());
        result.setSize(productPage.getSize());
        result.setTotalElements(productPage.getTotalElements());
        result.setTotalPages(productPage.getTotalPages());

        return result;
    }

    public ResultPaginationDTO getWarehouseReport(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        LocalDateTime snapshotDate = startDate.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        snapshotDate = snapshotDate.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        List<Object[]> snapshotResults = inventorySnapshotRepository.findLatestSnapshotBeforeDate(startDate);
        Map<String, Integer> openingStockMap = new HashMap<>();
        for (Object[] result : snapshotResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            openingStockMap.put(productCode, quantity != null ? quantity : 0);
        }

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

        List<Object[]> totalInResults = stockInDetailRepository.getTotalIn(startDate, endDate);
        Map<String, Integer> totalInMap = new HashMap<>();
        for (Object[] result : totalInResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            totalInMap.put(productCode, quantity != null ? quantity : 0);
        }

        List<Object[]> totalOutResults = stockOutDetailRepository.getTotalOut(startDate, endDate);
        Map<String, Integer> totalOutMap = new HashMap<>();
        for (Object[] result : totalOutResults) {
            String productCode = (String) result[0];
            Integer quantity = ((Number) result[1]).intValue();
            totalOutMap.put(productCode, quantity != null ? quantity : 0);
        }

        List<Product> products = productRepository.findAll();
        Map<String, String> productNameMap = products.stream()
                .collect(Collectors.toMap(Product::getProductCode, product -> product.getName() != null ? product.getName() : "Không có tên"));

        Set<String> allProductCodes = new HashSet<>();
        allProductCodes.addAll(openingStockMap.keySet());
        allProductCodes.addAll(totalInMap.keySet());
        allProductCodes.addAll(totalOutMap.keySet());

        List<WarehouseReportDTO> report = new ArrayList<>();
        for (String productCode : allProductCodes) {
            WarehouseReportDTO dto = new WarehouseReportDTO();
            dto.setProductCode(productCode);
            dto.setProductName(productNameMap.getOrDefault(productCode, "Không có tên"));
            dto.setOpeningStock(openingStockMap.getOrDefault(productCode, 0));
            dto.setTotalIn(totalInMap.getOrDefault(productCode, 0));
            dto.setTotalOut(totalOutMap.getOrDefault(productCode, 0));
            dto.setClosingStock(dto.getOpeningStock() + dto.getTotalIn() - dto.getTotalOut());
            report.add(dto);
        }

        // Phân trang thủ công
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, report.size());
        List<WarehouseReportDTO> pagedReport = report.subList(fromIndex, toIndex);

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setContent(pagedReport);
        result.setPage(page);
        result.setSize(size);
        result.setTotalElements(report.size());
        result.setTotalPages((int) Math.ceil((double) report.size() / size));

        return result;
    }

    public byte[] exportProductReportToExcel(String filterType) throws IOException {
        List<ProductReportDTO> report = (List<ProductReportDTO>) getProductReport(filterType, 0, Integer.MAX_VALUE).getContent();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo sản phẩm");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã hàng", "Tên Sản Phẩm", "Ngày hết hạn", "Ngày xuất kho gần nhất", "Số lượng tồn hiện tại"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (ProductReportDTO dto : report) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getProductCode());
            row.createCell(1).setCellValue(dto.getProductName() != null ? dto.getProductName() : "Không có tên");
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}