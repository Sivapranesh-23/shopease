package com.shopease.dto.response;

import com.shopease.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Order detail returned to customers and admins.
 */
public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal shippingTotal,
        BigDecimal taxTotal,
        BigDecimal grandTotal,
        String shippingAddress,
        String shippingCarrier,
        String trackingNumber,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
