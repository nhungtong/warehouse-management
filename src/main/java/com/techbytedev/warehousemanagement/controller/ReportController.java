package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.response.ProductReportDTO;
import com.techbytedev.warehousemanagement.dto.response.WarehouseReportDTO;
import com.techbytedev.warehousemanagement.service.ReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/products")
    public List<ProductReportDTO> getProductReport(@RequestParam(required = false) String filterType) {
        return reportService.getProductReport(filterType);
    }

    @GetMapping("/products/export")
    public ResponseEntity<byte[]> exportProductReportToExcel(@RequestParam(required = false) String filterType) throws IOException {
        byte[] excelBytes = reportService.exportProductReportToExcel(filterType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "product_report.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/warehouse")
    public List<WarehouseReportDTO> getWarehouseReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        if (startDate == null) startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        if (endDate == null) endDate = LocalDateTime.now();
        return reportService.getWarehouseReport(startDate, endDate);
    }

    @GetMapping("/warehouse/export")
    public ResponseEntity<byte[]> exportWarehouseReportToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws IOException {
        if (startDate == null) startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        if (endDate == null) endDate = LocalDateTime.now();
        List<WarehouseReportDTO> report = reportService.getWarehouseReport(startDate, endDate);

        // Tạo file Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo kho hàng");

        // Tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã hàng", "Tên Sản Phẩm", "Số lượng đầu kỳ", "Tổng nhập trong kỳ", "Tổng xuất trong kỳ", "Tồn kho cuối kỳ"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Dữ liệu
        int rowNum = 1;
        for (WarehouseReportDTO dto : report) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getProductCode());
            row.createCell(1).setCellValue(dto.getProductName() != null ? dto.getProductName() : "Không có tên"); // Thêm cột tên sản phẩm
            row.createCell(2).setCellValue(dto.getOpeningStock());
            row.createCell(3).setCellValue(dto.getTotalIn());
            row.createCell(4).setCellValue(dto.getTotalOut());
            row.createCell(5).setCellValue(dto.getClosingStock());
        }

        // Xuất file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        responseHeaders.setContentDispositionFormData("attachment", "warehouse_report.xlsx");
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(out.toByteArray());
    }
}