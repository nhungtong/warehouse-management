package com.techbytedev.warehousemanagement.controller;

import com.techbytedev.warehousemanagement.dto.request.AuthRequest;
import com.techbytedev.warehousemanagement.dto.request.RegisterRequest;
import com.techbytedev.warehousemanagement.dto.request.ResetPasswordRequest;
import com.techbytedev.warehousemanagement.dto.response.AuthResponse;
import com.techbytedev.warehousemanagement.dto.response.UserResponse;
import com.techbytedev.warehousemanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            response.setMessage("Login successful");
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid username or password");
            errorResponse.setSuccess(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // Các phương thức khác giữ nguyên...
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws MessagingException {
        authService.forgotPassword(email);
        return ResponseEntity.ok("Verification code sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(
            @RequestParam String email,
            @RequestParam String fullName) {
        return ResponseEntity.ok(authService.googleLogin(email, fullName));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        return ResponseEntity.ok(authService.getProfile());
    }
}