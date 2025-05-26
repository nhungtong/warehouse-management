package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.entity.Setting;
import com.techbytedev.warehousemanagement.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    // Lấy danh sách tất cả thiết lập
    @GetMapping("/list")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'GET')")
    public ResponseEntity<List<Setting>> getAllSettings() {
        try {
            return ResponseEntity.ok(settingService.getAllSettings());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Tạo mới thiết lập
    @PostMapping("/create")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'POST')")
    public ResponseEntity<Map<String, String>> createSetting(
            @RequestBody Map<String, String> request) {
        try {
            String result = settingService.createSetting(
                    request.get("key"),
                    request.get("value"),
                    request.get("description"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Cập nhật thiết lập
    @PutMapping("/update")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'PUT')")
    public ResponseEntity<Map<String, String>> updateSetting(
            @RequestBody Map<String, String> request) {
        try {
            settingService.updateSetting(
                    request.get("key"),
                    request.get("value"),
                    request.get("description"));
            return ResponseEntity.ok(Map.of("message", "Cập nhật thiết lập thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Xóa thiết lập
    @DeleteMapping("/delete")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'DELETE')")
    public ResponseEntity<Map<String, String>> deleteSetting(@RequestParam String key) {
        try {
            String result = settingService.deleteSetting(key);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Lấy trạng thái cảnh báo tồn kho thấp
    @GetMapping("/low-stock-alert-enabled")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'GET')")
    public ResponseEntity<Boolean> isLowStockAlertEnabled() {
        return ResponseEntity.ok(settingService.isLowStockAlertEnabled());
    }

    // Lấy danh sách email cảnh báo
    @GetMapping("/low-stock-alert-emails")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/settings/**', 'GET')")
    public ResponseEntity<String> getLowStockAlertEmails() {
        return ResponseEntity.ok(settingService.getLowStockAlertEmails());
    }
}