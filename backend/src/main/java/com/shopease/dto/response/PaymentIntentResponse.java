package com.shopease.dto.response;

import com.shopease.domain.enums.PaymentStatus;

import java.math.BigDecimal;

/**
 * Result of creating a payment intent / charge via the (stubbed) provider.
 */
public record PaymentIntentResponse(
        String paymentId,
        String providerPaymentId,
        PaymentStatus status,
        BigDecimal amount,
        String currency,
        String clientSecret
) {}
