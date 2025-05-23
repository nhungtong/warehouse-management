package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.entity.Supplier;
import com.techbytedev.warehousemanagement.repository.SupplierRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityExistsException;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers;
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        // Kiểm tra trùng tên nhà cung cấp
        supplierRepository.findByName(supplier.getName())
                .ifPresent(item -> {
                    throw new EntityExistsException("Nhà cung cấp với tên '" + item.getName() + "' đã tồn tại");
                });

        // Lưu vào database
        Supplier savedSupplier = supplierRepository.save(supplier);

        return savedSupplier;
    }
}