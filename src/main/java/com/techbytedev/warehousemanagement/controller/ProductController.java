package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/count")
    public long count() {
         return productService.countAllProducts();
    }
}
