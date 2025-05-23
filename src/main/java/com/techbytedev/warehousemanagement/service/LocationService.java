package com.techbytedev.warehousemanagement.service;

import com.techbytedev.warehousemanagement.entity.Location;
import com.techbytedev.warehousemanagement.repository.LocationRepository;
import jakarta.persistence.EntityExistsException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return locations;
    }

    @Transactional
    public Location createLocation(Location location) {
        // Kiểm tra trùng tên vị trí lưu trữ
        locationRepository.findByName(location.getName())
                .ifPresent(item -> {
                    throw new EntityExistsException(
                            "Vị trí lưu trữ với tên '" + item.getName() + "' đã tồn tại");
                });

        // Lưu vào database
        Location savedLocation = locationRepository.save(location);

        // Chuyển Entity thành DTO để trả về
        return savedLocation;
    }
}