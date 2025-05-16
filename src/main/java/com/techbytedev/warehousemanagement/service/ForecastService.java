package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.DemandForecastDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.StockOutDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ForecastService {

    @Autowired
    private StockOutDetailRepository stockOutDetailRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<DemandForecastDTO> getDemandForecast(String productCode) {
        LocalDateTime now = LocalDateTime.now(); // 10:55 AM, 16/05/2025
        LocalDateTime fourWeeksAgo = now.minusWeeks(4);

        // Lấy dữ liệu xuất kho 4 tuần gần nhất, chia thành 4 tuần
        List<Object[]> weeklyOut = stockOutDetailRepository.getWeeklyOutByProduct(fourWeeksAgo, now);
        Map<String, List<Object[]>> outByProduct = weeklyOut.stream()
                .collect(Collectors.groupingBy(result -> (String) result[0]));

        List<DemandForecastDTO> forecasts = new ArrayList<>();
        if (productCode != null && !outByProduct.containsKey(productCode)) {
            return forecasts; // Không có dữ liệu xuất kho cho sản phẩm này
        }

        // Nếu không có productCode, lấy tất cả sản phẩm; nếu có, chỉ lấy sản phẩm được chọn
        List<String> productCodes = productCode != null ? List.of(productCode) : new ArrayList<>(outByProduct.keySet());

        for (String code : productCodes) {
            // Tính trung bình xuất kho 4 tuần gần nhất
            List<Object[]> outData = outByProduct.getOrDefault(code, new ArrayList<>());
            Integer totalOut = outData.stream()
                    .mapToInt(result -> ((Number) result[1]).intValue())
                    .sum();
            Integer avgForecast = totalOut / 4; // SMA cho 4 tuần

            // Lấy tổng tồn kho hiện tại cho sản phẩm (có thể có nhiều Inventory cho cùng productCode)
            List<Inventory> inventories = inventoryRepository.findByProductCode(code);
            Integer currentStock = inventories.stream()
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            // Tính lượng nhập gợi ý
            Integer suggestedIn = Math.max(0, avgForecast - currentStock);

            DemandForecastDTO dto = new DemandForecastDTO();
            dto.setProductCode(code);
            dto.setForecastQuantity(avgForecast);
            dto.setCurrentStock(currentStock);
            dto.setSuggestedIn(suggestedIn);
            forecasts.add(dto);
        }

        return forecasts;
    }
}