package com.shopease.domain.enums;

/**
 * Payment lifecycle states tracked against a {@code Payment} record.
 */
public enum PaymentStatus {
    PENDING,
    SUCCEEDED,
    FAILED,
    REFUNDED
}
