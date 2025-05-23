package com.techbytedev.warehousemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Bật tính năng lập lịch để @Scheduled hoạt động

public class WarehouseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagementApplication.class, args);
    }

}
