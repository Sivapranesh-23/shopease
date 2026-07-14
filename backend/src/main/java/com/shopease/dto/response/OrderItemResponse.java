package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * Immutable order line.
 */
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String productSku,
        String imageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
