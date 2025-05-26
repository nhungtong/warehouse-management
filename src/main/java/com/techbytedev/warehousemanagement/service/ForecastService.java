package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.response.DemandForecastDTO;
import com.techbytedev.warehousemanagement.entity.Inventory;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.repository.InventoryRepository;
import com.techbytedev.warehousemanagement.repository.ProductRepository;
import com.techbytedev.warehousemanagement.repository.StockOutDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForecastService {

    @Autowired
    private StockOutDetailRepository stockOutDetailRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<DemandForecastDTO> getDemandForecast(String productCode) {
        LocalDateTime now = LocalDateTime.now(); // 04:45 PM, 24/05/2025
        LocalDateTime fourWeeksAgo = now.minusWeeks(4);

        // Lấy dữ liệu xuất kho 4 tuần gần nhất, chia thành 4 tuần
        List<Object[]> weeklyOut = stockOutDetailRepository.getWeeklyOutByProduct(fourWeeksAgo, now);
        Map<String, List<Object[]>> outByProduct = weeklyOut.stream()
                .collect(Collectors.groupingBy(result -> (String) result[0]));

        // Lấy sản phẩm từ bảng products
        List<Product> products;
        if (productCode != null) {
            Optional<Product> productOptional = productRepository.findByProductCode(productCode);
            if (productOptional.isEmpty()) {
                return new ArrayList<>(); // Không tìm thấy sản phẩm với productCode này
            }
            products = List.of(productOptional.get()); // Chuyển Optional<Product> thành List<Product> với 1 phần tử
        } else {
            products = productRepository.findAll();
        }

        List<DemandForecastDTO> forecasts = new ArrayList<>();

        for (Product product : products) {
            String code = product.getProductCode();

            // Tính trung bình xuất kho 4 tuần gần nhất
            List<Object[]> outData = outByProduct.getOrDefault(code, new ArrayList<>());
            int weekCount = outData.size();
            Integer totalOut = outData.stream()
                    .mapToInt(result -> ((Number) result[1]).intValue())
                    .sum();
            Integer avgForecast = weekCount > 0 ? totalOut / weekCount : 0; // SMA, nếu không có dữ liệu thì dự báo = 0

            // Lấy tổng tồn kho hiện tại cho sản phẩm
            List<Inventory> inventories = inventoryRepository.findByProductCode(code);
            Integer currentStock = inventories.stream()
                    .mapToInt(Inventory::getQuantity)
                    .sum();

            // Tính lượng nhập gợi ý
            Integer suggestedIn = Math.max(0, avgForecast - currentStock);

            DemandForecastDTO dto = new DemandForecastDTO();
            dto.setProductCode(code);
            dto.setProductName(product.getName() != null ? product.getName() : "Không có tên");
            dto.setForecastQuantity(avgForecast);
            dto.setCurrentStock(currentStock);
            dto.setSuggestedIn(suggestedIn);
            forecasts.add(dto);
        }

        return forecasts;
    }
}