package com.shopease.controller;

import com.shopease.domain.User;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.UserResponse;
import com.shopease.mapper.UserMapper;
import com.shopease.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SecurityUtils securityUtils;
    private final UserMapper userMapper;

    @Operation(summary = "Get current user profile")
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> profile() {
        User user = securityUtils.requireCurrentDomainUser();
        return ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user)));
    }
}
