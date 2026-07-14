package com.shopease.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Category creation and update request payload.
 * Includes validation for all fields.
 */
public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Category slug is required")
        @Size(min = 2, max = 100, message = "Category slug must be between 2 and 100 characters")
        String slug,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        Long parentId
) {}
