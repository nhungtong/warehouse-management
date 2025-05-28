package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.StockOutRequest;
import com.techbytedev.warehousemanagement.dto.response.StockOutFormDTO;
import com.techbytedev.warehousemanagement.dto.response.StockOutFormDetailViewDTO;
import com.techbytedev.warehousemanagement.service.StockOutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock-out")
public class StockOutController {
    private final StockOutService stockOutService;

    public StockOutController(StockOutService stockOutService) {
        this.stockOutService = stockOutService;
    }
    @GetMapping("/count")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stock-out/**', 'GET')")
    public long count() {
        return stockOutService.getTotalStockOut();
    }
    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/stock-out/**', 'POST')")
    public ResponseEntity<?> handleStockOut(
            @RequestBody StockOutRequest request
    ) {
        try {
            stockOutService.handleStockOut(request);
            return ResponseEntity.ok("Xuất kho thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // lấy danh sách phiếu xuất hàng
    @GetMapping("/forms")
    public ResponseEntity<Map<String, Object>> getStockOutForms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<StockOutFormDTO> formPage = stockOutService.getAllForms(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", formPage.getContent());
        response.put("pageNumber", formPage.getNumber() + 1);
        response.put("pageSize", formPage.getSize());
        response.put("totalPages", formPage.getTotalPages());
        response.put("totalElements", formPage.getTotalElements());
        response.put("last", formPage.isLast());

        return ResponseEntity.ok(response);
    }

    // hiển thị chi tiết phiếu xuất
    @GetMapping("/forms/{id}")
    public StockOutFormDetailViewDTO getFormDetails(@PathVariable Integer id) {
        return stockOutService.getFormWithDetails(id);
    }

}
