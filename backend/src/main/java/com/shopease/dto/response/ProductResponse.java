package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * Public product representation used in storefront and admin responses.
 */
public record ProductResponse(
        Long id,
        String name,
        String slug,
        String sku,
        String summary,
        String description,
        BigDecimal price,
        BigDecimal compareAtPrice,
        Integer stock,
        String imageUrl,
        boolean active,
        Double rating,
        Integer reviewCount,
        Long categoryId,
        String categoryName,
        String categorySlug
) {}
