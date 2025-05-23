package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.StockCheckResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service // Đảm bảo Spring nhận diện class này là một bean
public class ExcelExportService {

    // Phương thức tạo file Excel từ danh sách kiểm kê
    public ByteArrayInputStream exportStockChecksToExcel(List<StockCheckResponse> stockChecks) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Tạo sheet mới trong file Excel
            Sheet sheet = workbook.createSheet("StockCheckReport");

            // Tạo header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Vị trí", "Số lượng hệ thống", 
                               "Số lượng thực tế", "Chênh lệch", "Trạng thái"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                // Tùy chỉnh style cho header
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu từ danh sách kiểm kê
            int rowNum = 1;
            for (StockCheckResponse stockCheck : stockChecks) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stockCheck.getProductCode());
                row.createCell(1).setCellValue(stockCheck.getProductName());
                row.createCell(2).setCellValue(stockCheck.getLocationName());
                row.createCell(3).setCellValue(stockCheck.getSystemQuantity());
                row.createCell(4).setCellValue(stockCheck.getActualQuantity());
                row.createCell(5).setCellValue(stockCheck.getDifference());
                row.createCell(6).setCellValue(stockCheck.getStatus());
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi workbook vào output stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
}