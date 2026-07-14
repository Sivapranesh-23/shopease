package com.shopease.service;

import com.shopease.domain.enums.PaymentStatus;
import com.shopease.dto.response.PaymentIntentResponse;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Payment operations. Provider integration is stubbed — swap the impl to use a real gateway.
 */
public interface PaymentService {

    PaymentIntentResponse createIntent(Long orderId, BigDecimal amount, String currency);

    PaymentStatus confirm(String providerPaymentId);

    /**
     * Handle a provider webhook event. Returns the resulting payment status.
     */
    PaymentStatus handleWebhook(String provider, String providerPaymentId, String eventType, Map<String, Object> payload);
}
