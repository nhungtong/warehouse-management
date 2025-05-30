package com.techbytedev.warehousemanagement.dto.request;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
}