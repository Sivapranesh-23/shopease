package com.shopease.service;

import com.shopease.dto.request.LoginRequest;
import com.shopease.dto.request.RegisterRequest;
import com.shopease.dto.response.AuthResponse;
import com.shopease.dto.response.UserResponse;
import jakarta.validation.Valid;

/**
 * Authentication operations.
 */
public interface AuthService {

    AuthResponse register(@Valid RegisterRequest request);

    AuthResponse login(@Valid LoginRequest request);

    AuthResponse refresh(String refreshToken);

    UserResponse me(Long userId);

    void logout(Long userId);
}
