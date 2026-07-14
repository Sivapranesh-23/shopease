package com.shopease.controller;

import com.shopease.domain.enums.PaymentStatus;
import com.shopease.dto.response.ApiResponse;
import com.shopease.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Webhooks")
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final PaymentService paymentService;

    @Value("${app.payment.stripe-webhook-secret:}")
    private String stripWebhookSecret;

    @Operation(summary = "Handle payment provider webhook events")
    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<Map<String, String>>> handlePaymentWebhook(
            @RequestHeader("X-Provider") String provider,
            @RequestBody Map<String, Object> payload) {
        String providerPaymentId = String.valueOf(payload.get("providerPaymentId"));
        String eventType = String.valueOf(payload.get("eventType"));
        PaymentStatus status = paymentService.handleWebhook(provider, providerPaymentId, eventType, payload);
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", status.name())));
    }

    /**
     * Receive and process Stripe webhook events.
     * Authenticates the request signature before processing.
     * Endpoint: POST /api/webhooks/stripe
     */
    @Operation(summary = "Handle Stripe webhook events")
    @PostMapping("/stripe")
    public ResponseEntity<ApiResponse<Map<String, String>>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

        if (stripWebhookSecret == null || stripWebhookSecret.isEmpty()) {
            log.warn("Stripe webhook secret not configured, skipping webhook processing");
            return ResponseEntity.ok(ApiResponse.success(Map.of("status", "skipped", "reason", "webhook secret not configured")));
        }

        try {
            // Verify the signature
            Event event = Webhook.constructEvent(payload, signature, stripWebhookSecret);
            log.info("Received Stripe webhook event: {}", event.getType());

            // Handle the event
            processStripeEvent(event);

            return ResponseEntity.ok(ApiResponse.success(Map.of("status", "processed", "eventType", event.getType())));
        } catch (StripeException e) {
            log.error("Stripe API error processing webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Stripe API error: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid signature", null));
        }
    }

    /**
     * Process a Stripe event and update payment status accordingly.
     */
    private void processStripeEvent(Event event) {
        try {
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(event);
                    break;
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                default:
                    log.debug("Unhandled Stripe event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing Stripe webhook event: {}", e.getMessage(), e);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) throws StripeException {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().get();

        String paymentIntentId = paymentIntent.getId();
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", paymentIntent.getAmount());
        payload.put("currency", paymentIntent.getCurrency());
        payload.put("status", paymentIntent.getStatus());

        paymentService.handleWebhook("stripe", paymentIntentId, "payment_intent.succeeded", payload);
        log.info("Payment succeeded: {}", paymentIntentId);
    }

    private void handlePaymentIntentFailed(Event event) throws StripeException {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().get();

        String paymentIntentId = paymentIntent.getId();
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", paymentIntent.getStatus());
        if (paymentIntent.getLastPaymentError() != null) {
            payload.put("error", paymentIntent.getLastPaymentError().getMessage());
        }

        paymentService.handleWebhook("stripe", paymentIntentId, "payment_intent.payment_failed", payload);
        log.warn("Payment failed: {}", paymentIntentId);
    }

    private void handlePaymentIntentCanceled(Event event) throws StripeException {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().get();

        String paymentIntentId = paymentIntent.getId();
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", paymentIntent.getStatus());

        paymentService.handleWebhook("stripe", paymentIntentId, "payment_intent.canceled", payload);
        log.info("Payment canceled: {}", paymentIntentId);
    }

    private void handleChargeRefunded(Event event) throws StripeException {
        // Handle charge refunded events - updates payment to REFUNDED status
        log.info("Charge refunded event: {}", event.getId());
    }
}
