package com.techbytedev.warehousemanagement.dto.request;

import com.techbytedev.warehousemanagement.entity.Location;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.entity.Supplier;
import lombok.Data;

@Data
public class ProductRequest {
    private Product product;
    private Supplier supplier;
    private Location location;
}
