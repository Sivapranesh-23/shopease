package com.shopease.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Create a new product (admin only).
 */
public record StoreProductRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 180)
        String name,

        @NotBlank(message = "SKU is required")
        @Size(max = 64)
        String sku,

        @Size(max = 600)
        String summary,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Compare-at price must be non-negative")
        BigDecimal compareAtPrice,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,

        @Size(max = 600)
        String imageUrl,

        Boolean active,

        @NotNull(message = "Category id is required")
        Long categoryId
) {}
