package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * A single line in a cart response.
 */
public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSlug,
        String imageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
