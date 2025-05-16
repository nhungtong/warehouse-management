package com.techbytedev.warehousemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.techbytedev.warehousemanagement.entity.Setting;
import com.techbytedev.warehousemanagement.service.SettingService;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;

    // API cập nhật thiết lập (chỉ admin)
    @PostMapping("/update")
    public String updateSetting(@RequestParam String key, @RequestParam String value, @RequestParam String description) {
        settingService.updateSetting(key, value, description);
        return "Cập nhật thiết lập thành công!";
    }

    // API tạo mới thiết lập (chỉ admin)
    @PostMapping("/create")
    public String createSetting(@RequestParam String key, @RequestParam String value, @RequestParam String description) {
        return settingService.createSetting(key, value, description);
    }

    // API xóa thiết lập (chỉ admin)
    @DeleteMapping("/delete")
    public String deleteSetting(@RequestParam String key) {
        return settingService.deleteSetting(key);
    }

    // API lấy danh sách tất cả thiết lập (chỉ admin)
    @GetMapping("/list")
    public List<Setting> getAllSettings() {
        return settingService.getAllSettings();
    }

    // API lấy trạng thái cảnh báo tồn kho thấp (có thể cho phép cả user thường)
    @GetMapping("/low-stock-alert-enabled")
    public boolean isLowStockAlertEnabled() {
        return settingService.isLowStockAlertEnabled();
    }

    // API lấy danh sách email cảnh báo (có thể cho phép cả user thường)
    @GetMapping("/low-stock-alert-emails")
    public String getLowStockAlertEmails() {
        return settingService.getLowStockAlertEmails();
    }
}


