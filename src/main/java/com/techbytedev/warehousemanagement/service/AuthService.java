package com.techbytedev.warehousemanagement.service;


import jakarta.mail.MessagingException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techbytedev.warehousemanagement.dto.request.AuthRequest;
import com.techbytedev.warehousemanagement.dto.request.RegisterRequest;
import com.techbytedev.warehousemanagement.dto.request.ResetPasswordRequest;
import com.techbytedev.warehousemanagement.dto.response.AuthResponse;
import com.techbytedev.warehousemanagement.dto.response.UserResponse;
import com.techbytedev.warehousemanagement.entity.PasswordResetToken;
import com.techbytedev.warehousemanagement.entity.Role;
import com.techbytedev.warehousemanagement.entity.User;
import com.techbytedev.warehousemanagement.repository.PasswordResetTokenRepository;
import com.techbytedev.warehousemanagement.repository.RoleRepository;
import com.techbytedev.warehousemanagement.repository.UserRepository;
import com.techbytedev.warehousemanagement.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserService userService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
            PasswordResetTokenRepository tokenRepository, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, AuthenticationManager authenticationManager,
            EmailService emailService, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userService = userService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        Role role = roleRepository.findByName("Customer")
                .orElseThrow(() -> new IllegalArgumentException("Customer role not found"));

        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);

        String jwt = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse(jwt);
        response.setUser(userService.convertToResponse(user));
        return response;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        String jwt = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse(jwt);
        response.setUser(userService.convertToResponse(user));
        return response;
    }

    public void forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String verificationCode = generateVerificationCode();
        tokenRepository.deleteByUserId(user.getId());
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        PasswordResetToken resetToken = new PasswordResetToken(verificationCode, user, expiryDate);
        tokenRepository.save(resetToken);

        emailService.sendVerificationCodeEmail(user.getEmail(), verificationCode);
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification code"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Verification code has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    public AuthResponse googleLogin(String email, String fullName) {
        UserResponse userResponse = userService.processGoogleUser(email, fullName);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found after Google login"));
        String jwt = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse(jwt);
        response.setUser(userResponse);
        return response;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public UserResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">>>>>>>>authentication:            " + authentication);
        System.out.println(">>>>>check: authentication # null: " + authentication != null);
        System.out.println(("auth.name = "+ authentication.getName()));
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return userService.convertToResponse(user);
        }
        return null;
    }
}