package com.shopease.controller;

import com.shopease.dto.request.LoginRequest;
import com.shopease.dto.request.RegisterRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.AuthResponse;
import com.shopease.dto.response.UserResponse;
import com.shopease.security.SecurityUtils;
import com.shopease.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Register a new customer account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @Operation(summary = "Login and obtain JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @Operation(summary = "Refresh the access token using a valid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @Operation(summary = "Get the authenticated user's profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        Long userId = securityUtils.requireCurrentUserId();
        UserResponse response = authService.me(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Logout (client-side token discard)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        Long userId = securityUtils.requireCurrentUserId();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.message("Logged out successfully"));
    }
}
