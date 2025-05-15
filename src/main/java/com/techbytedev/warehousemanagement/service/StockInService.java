package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.dto.request.ProductRequest;
import com.techbytedev.warehousemanagement.entity.Location;
import com.techbytedev.warehousemanagement.entity.Product;
import com.techbytedev.warehousemanagement.entity.Supplier;
import com.techbytedev.warehousemanagement.repository.*;
import org.springframework.stereotype.Service;

@Service
public class StockInService {
    private final StockInDetailRepository stockInDetailRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final LocationRepository locationRepository;
    private final StockInFormRepository stockInFormRepository;
    private final InventoryRepository inventoryRepository;

    public StockInService(StockInDetailRepository stockInDetailRepository, ProductRepository productRepository, SupplierRepository supplierRepository, LocationRepository locationRepository, StockInFormRepository stockInFormRepository, InventoryRepository inventoryRepository) {
        this.stockInDetailRepository = stockInDetailRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.locationRepository = locationRepository;
        this.stockInFormRepository = stockInFormRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public long getTotalStockIn() {
        return stockInDetailRepository.getTotalQuantity();
    }
    private ProductRequest getOrCreateProduct(String productCode, String productName, String unit, String supplierName, String locationName) {
        Supplier supplier = getOrCreateSupplier(supplierName);
        Product product = productRepository.findByProductCode(productCode)
                .orElseGet(() -> {
                    Product newProduct = new Product();
                    newProduct.setProductCode(productCode);
                    newProduct.setName(productName);
                    newProduct.setUnit(unit);
                    newProduct.setSupplier(supplier);
                    productRepository.save(newProduct);
                    return newProduct;
                });

        Supplier supplier1 = supplierRepository.findByName(supplierName)
                .orElseGet(() -> {
                    Supplier newSupplier = new Supplier();
                    newSupplier.setName(supplierName);
                    return supplierRepository.save(newSupplier);
                });

        Location location = locationRepository.findByName(locationName)
                .orElseGet(() -> {
                    Location newLocation = new Location();
                    newLocation.setName(locationName);
                    return locationRepository.save(newLocation);
                });

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProduct(product);
        productRequest.setSupplier(supplier1);
        productRequest.setLocation(location);

        return productRequest;
    }

    private Supplier getOrCreateSupplier(String name) {
        return supplierRepository.findByName(name)
                .orElseGet(() -> supplierRepository.save(new Supplier(name)));
    }
    private Location getOrCreateLocation(String name) {
        return locationRepository.findByName(name)
                .orElseGet(() -> locationRepository.save(new Location(name)));
    }
        // hàm này cho phép tải file
        // mã hàng dạng combobox
        // đơn vị tính dạng combobox
        // trường hợp mã hàng đã tồn tại, người dùng chỉ cần nhập mã là ra các trường còn lại
        // trường hợp mã hàng mới, người dùng phải nhập tay toàn bộ riêng nhà cung cấp và vị trí lưu thì lấy tên, và cho phép người dùng nhập nếu là tên mới.
        // lưu mã hàng mới vào bảng product khi là mã hàng mới.
        // lưu location mới vào bảng location khi là tên vị trí mới
        // lưu nhà cung cấp mới vào bảng suppliers khi là tên nhà cung cấp mới
        // lưu dữ liệu vào bảng inventory, nếu id của hàng đã tồn tại trong bảng thì cộng dồn số lượng
        // lưu dữ liệu vào bảng stockInForm và stocInDetails
}
