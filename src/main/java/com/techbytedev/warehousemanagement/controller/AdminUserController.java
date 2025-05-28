package com.techbytedev.warehousemanagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.techbytedev.warehousemanagement.dto.request.UserCreateRequest;
import com.techbytedev.warehousemanagement.dto.request.UserUpdateRequest;
import com.techbytedev.warehousemanagement.dto.response.*;

import com.techbytedev.warehousemanagement.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public CustomPageResponse<UserResponse> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        int adjustedPage = page - 1;
        if (adjustedPage < 0) adjustedPage = 0;
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(direction, sortParams[0]));
        Page<UserResponse> userPage = userService.getAllUsers(pageable);

        return new CustomPageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.getNumberOfElements(),
                userPage.isEmpty()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public UserResponse getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users', 'POST')")
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/search")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'GET')")
    public CustomPageResponse<UserResponse> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        int adjustedPage = page - 1;
        if (adjustedPage < 0) adjustedPage = 0;
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(direction, sortParams[0]));
        Page<UserResponse> userPage = userService.searchUsers(username, email, roleName, isActive, pageable);

        return new CustomPageResponse<>(
                userPage.getContent(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast(),
                userPage.getNumberOfElements(),
                userPage.isEmpty()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public UserResponse updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'DELETE')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{id}/assign-admin")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public ResponseEntity<UserResponse> assignAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.assignAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/remove-admin")
    @PreAuthorize("@permissionChecker.hasPermission(authentication, '/api/admin/users/**', 'PUT')")
    public ResponseEntity<UserResponse> removeAdminRole(@PathVariable Integer id) {
        UserResponse updatedUser = userService.removeAdminRole(id);
        return ResponseEntity.ok(updatedUser);
    }
}