package com.shopease.dto.response;

import com.shopease.domain.enums.UserRole;

/**
 * Public user representation (never exposes the password hash).
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        boolean active
) {}
