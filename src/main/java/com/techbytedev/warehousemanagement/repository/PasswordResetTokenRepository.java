package com.techbytedev.warehousemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techbytedev.warehousemanagement.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(Integer userId); // Thay Long thành Integer
}