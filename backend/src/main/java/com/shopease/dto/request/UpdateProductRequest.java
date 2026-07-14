package com.shopease.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Partial update of a product. All fields are optional; {@code null} means "leave unchanged".
 */
public record UpdateProductRequest(
        @Size(max = 180)
        String name,

        @Size(max = 64)
        String sku,

        @Size(max = 600)
        String summary,

        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Compare-at price must be non-negative")
        BigDecimal compareAtPrice,

        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,

        @Size(max = 600)
        String imageUrl,

        Boolean active,

        Long categoryId
) {}
