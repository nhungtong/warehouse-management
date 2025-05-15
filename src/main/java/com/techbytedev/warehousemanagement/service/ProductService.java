package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public long countByProductId(Integer id) {
        return productRepository.countByProductId(id);
    }
}
