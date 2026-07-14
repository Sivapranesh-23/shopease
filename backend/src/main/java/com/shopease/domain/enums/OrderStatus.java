package com.shopease.domain.enums;

/**
 * Lifecycle states for a customer order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
