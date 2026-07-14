package com.shopease.controller;

import com.shopease.domain.enums.PaymentStatus;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.PaymentIntentResponse;
import com.shopease.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${app.payment.stripe-publishable-key:}")
    private String stripePublishableKey;

    /**
     * Get Stripe publishable key config.
     * Called from frontend: GET /api/payments/config
     */
    @Operation(summary = "Get Stripe publishable key configuration")
    @GetMapping("/payments/config")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPaymentConfig() {
        return ResponseEntity.ok(ApiResponse.success(Map.of("publishableKey", stripePublishableKey != null ? stripePublishableKey : "")));
    }

    /**
     * Create a payment intent for an order.
     * Called from frontend: POST /api/orders/{orderId}/payment/intent
     */
    @Operation(summary = "Create a payment intent for an order")
    @PostMapping("/orders/{orderId}/payment/intent")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createPaymentIntent(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.getOrDefault("amount", "0").toString());
        String currency = body.getOrDefault("currency", "usd").toString().toUpperCase();
        PaymentIntentResponse response = paymentService.createIntent(orderId, amount, currency);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Legacy endpoint: Create a payment intent
     * Path: POST /api/payments/intent/{orderId}
     */
    @Operation(summary = "Create a payment intent (legacy endpoint)")
    @PostMapping("/payments/intent/{orderId}")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createIntent(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.getOrDefault("amount", "0").toString());
        String currency = body.getOrDefault("currency", "USD").toString();
        PaymentIntentResponse response = paymentService.createIntent(orderId, amount, currency);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Confirm a payment intent.
     * Called from frontend: POST /api/payments/{paymentIntentId}/confirm
     */
    @Operation(summary = "Confirm a payment after Stripe processing")
    @PostMapping("/payments/{paymentIntentId}/confirm")
    public ResponseEntity<ApiResponse<Map<String, String>>> confirmPayment(
            @PathVariable String paymentIntentId) {
        PaymentStatus status = paymentService.confirm(paymentIntentId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", status.name())));
    }

    /**
     * Legacy endpoint: Confirm a payment
     * Path: POST /api/payments/confirm
     */
    @Operation(summary = "Confirm a payment (legacy endpoint)")
    @PostMapping("/payments/confirm")
    public ResponseEntity<ApiResponse<Map<String, String>>> confirmPaymentLegacy(
            @RequestBody Map<String, String> body) {
        String providerPaymentId = body.get("providerPaymentId");
        PaymentStatus status = paymentService.confirm(providerPaymentId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", status.name())));
    }

    /**
     * Webhook endpoint for generic payment provider events.
     * Path: POST /api/payments/webhook
     */
    @Operation(summary = "Handle payment provider webhook events (generic)")
    @PostMapping("/payments/webhook")
    public ResponseEntity<ApiResponse<Map<String, String>>> handlePaymentWebhook(
            @RequestHeader("X-Provider") String provider,
            @RequestBody Map<String, Object> payload) {
        String providerPaymentId = String.valueOf(payload.get("providerPaymentId"));
        String eventType = String.valueOf(payload.get("eventType"));
        PaymentStatus status = paymentService.handleWebhook(provider, providerPaymentId, eventType, payload);
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", status.name())));
    }
}
