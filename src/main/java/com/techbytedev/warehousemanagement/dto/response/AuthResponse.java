package com.techbytedev.warehousemanagement.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserResponse user;
    private String message; // Thêm trường message
    private boolean success; // Thêm trường success để chỉ trạng thái

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse() {
    }
}