package com.techbytedev.warehousemanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techbytedev.warehousemanagement.entity.Setting;
import com.techbytedev.warehousemanagement.repository.SettingRepository;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;

    public boolean isLowStockAlertEnabled() {
        Setting setting = settingRepository.findByKey("low_stock_alert_enabled");
        return setting != null && Boolean.parseBoolean(setting.getValue());
    }

    public String getLowStockAlertEmails() {
        Setting setting = settingRepository.findByKey("low_stock_alert_emails");
        return setting != null ? setting.getValue() : "";
    }

    // Thêm phương thức cho stock_threshold
    public Integer getStockThreshold() {
        Setting setting = settingRepository.findByKey("stock_threshold");
        return setting != null ? Integer.parseInt(setting.getValue()) : 0; // Giá trị mặc định là 0 nếu không có
    }

    // Thêm phương thức cho notification_frequency
    public String getNotificationFrequency() {
        Setting setting = settingRepository.findByKey("notification_frequency");
        return setting != null ? setting.getValue() : "daily"; // Giá trị mặc định là "daily"
    }

    public void updateSetting(String key, String value, String description) {
        Setting setting = settingRepository.findByKey(key);
        if (setting == null) {
            setting = new Setting();
            setting.setKey(key);
        }
        setting.setValue(value);
        setting.setDescription(description);
        settingRepository.save(setting);
    }

    // Tạo mới thiết lập
    public String createSetting(String key, String value, String description) {
        if (settingRepository.findByKey(key) != null) {
            return "Thiết lập với key " + key + " đã tồn tại!";
        }
        Setting setting = new Setting();
        setting.setKey(key);
        setting.setValue(value);
        setting.setDescription(description);
        settingRepository.save(setting);
        return "Tạo thiết lập mới thành công!";
    }

    // Xóa thiết lập
    public String deleteSetting(String key) {
        Setting setting = settingRepository.findByKey(key);
        if (setting == null) {
            return "Không tìm thấy thiết lập với key " + key + "!";
        }
        settingRepository.delete(setting);
        return "Xóa thiết lập thành công!";
    }

    // Lấy danh sách tất cả thiết lập
    public List<Setting> getAllSettings() {
        return settingRepository.findAll();
    }
}