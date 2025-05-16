package com.techbytedev.warehousemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techbytedev.warehousemanagement.entity.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {
    Setting findByKey(String key);
}