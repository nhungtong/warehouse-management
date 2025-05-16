package com.techbytedev.warehousemanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.techbytedev.warehousemanagement.service.PermissionService;
import com.techbytedev.warehousemanagement.service.UserService;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public PermissionInterceptorConfiguration(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor(userService, permissionService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
            "/api/auth/**",
            "/login/oauth2/**",
            "/api/design/**",
            "/api/categories/**",
            "/api/cms/**",
            "/images/**",
            "/api/products/**",
            "/api/reports/**",
            "/api/forecast/**",
            "/**.hot-update.json",
            "/**.hot-update.js","/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
        };
        registry.addInterceptor(permissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}