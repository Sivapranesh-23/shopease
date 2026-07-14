package com.shopease.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cart snapshot returned to the storefront.
 */
public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        int totalQuantity
) {}
