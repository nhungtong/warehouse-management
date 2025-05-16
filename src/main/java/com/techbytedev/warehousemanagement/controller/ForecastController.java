package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.response.DemandForecastDTO;
import com.techbytedev.warehousemanagement.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    @Autowired
    private ForecastService forecastService;

    @GetMapping("/demand")
    public List<DemandForecastDTO> getDemandForecast(@RequestParam(required = false) String productCode) {
        return forecastService.getDemandForecast(productCode);
    }
}