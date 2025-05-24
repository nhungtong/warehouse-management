package com.techbytedev.warehousemanagement.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockCheckScheduler {
    private final StockCheckService stockCheckService;

    public StockCheckScheduler(StockCheckService stockCheckService) {
        this.stockCheckService = stockCheckService;
    }

    // Lên lịch chạy vào 23:59 ngày cuối cùng của mỗi tháng
    @Scheduled(cron = "0 59 23 L * ?")
    public void scheduleMonthlyStockCheckReport() {
        stockCheckService.sendMonthlyReportToAdmins();
    }
}


// package com.techbytedev.warehousemanagement.service;

// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// import java.time.LocalDateTime;
// import java.time.temporal.ChronoUnit;

// @Component
// public class StockCheckScheduler {
//     private final StockCheckService stockCheckService;

//     public StockCheckScheduler(StockCheckService stockCheckService) {
//         this.stockCheckService = stockCheckService;
//     }

//     // Phương thức chạy báo cáo kiểm kê để kiểm tra
//     @Scheduled(fixedDelay = Long.MAX_VALUE) // Ngăn chạy lại sau khi chạy lần đầu
//     public void scheduleMonthlyStockCheckReport() {
//         // Tính thời gian hiện tại và thời gian chạy mong muốn (sau 2 phút)
//         LocalDateTime now = LocalDateTime.now();
//         LocalDateTime runTime = now.plus(2, ChronoUnit.MINUTES); // Chạy sau 2 phút

//         // In log để kiểm tra
//         System.out.println("Thời gian hiện tại: " + now);
//         System.out.println("Thời gian chạy dự kiến: " + runTime);

//         // Chờ đến thời gian chạy (09:55 AM)
//         long delayInMillis = ChronoUnit.MILLIS.between(now, runTime);
//         if (delayInMillis > 0) {
//             try {
//                 Thread.sleep(delayInMillis); // Tạm dừng luồng cho đến thời gian chạy
//             } catch (InterruptedException e) {
//                 System.err.println("Lỗi khi chờ: " + e.getMessage());
//             }
//         }

//         // Gọi phương thức gửi báo cáo
//         stockCheckService.sendMonthlyReportToAdmins();
//         System.out.println("Báo cáo kiểm kê đã được gửi lúc: " + LocalDateTime.now());
//     }
// }