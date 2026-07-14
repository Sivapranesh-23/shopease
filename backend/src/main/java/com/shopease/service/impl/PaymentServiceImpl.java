package com.shopease.service.impl;

import com.shopease.domain.Order;
import com.shopease.domain.Payment;
import com.shopease.domain.enums.PaymentStatus;
import com.shopease.domain.enums.OrderStatus;
import com.shopease.dto.response.PaymentIntentResponse;
import com.shopease.exception.PaymentFailedException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.PaymentRepository;
import com.shopease.service.CartService;
import com.shopease.service.NotificationService;
import com.shopease.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Real Stripe payment provider integration.
 * Handles payment intents, confirmations, and webhooks.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final NotificationService notificationService;
    private final boolean stripeEnabled;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              CartService cartService,
                              NotificationService notificationService,
                              @Value("${app.payment.stripe-secret-key:}") String stripeSecretKey) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.notificationService = notificationService;
        this.stripeEnabled = stripeSecretKey != null && !stripeSecretKey.isEmpty();
    }

    @Override
    public PaymentIntentResponse createIntent(Long orderId, BigDecimal amount, String currency) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        try {
            // Idempotency: Check if there's already a payment record for this order
            Optional<Payment> existingPaymentOpt = paymentRepository.findByOrderId(orderId);
            if (existingPaymentOpt.isPresent()) {
                Payment existingPayment = existingPaymentOpt.get();
                if (existingPayment.getStatus() == PaymentStatus.SUCCEEDED) {
                    throw new PaymentFailedException("Order has already been paid.");
                }

                if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                    if (!stripeEnabled) {
                        return new PaymentIntentResponse(
                                existingPayment.getId().toString(),
                                existingPayment.getProviderPaymentId(),
                                existingPayment.getStatus(),
                                existingPayment.getAmount(),
                                existingPayment.getCurrency(),
                                "stub_secret_" + existingPayment.getProviderPaymentId()
                        );
                    } else {
                        PaymentIntent intent = PaymentIntent.retrieve(existingPayment.getProviderPaymentId());
                        PaymentStatus syncedStatus = mapStripeStatus(intent.getStatus());
                        if (syncedStatus != existingPayment.getStatus()) {
                            existingPayment.setStatus(syncedStatus);
                            paymentRepository.save(existingPayment);
                            if (syncedStatus == PaymentStatus.SUCCEEDED) {
                                if (order.getStatus() != OrderStatus.PAID) {
                                    order.setStatus(OrderStatus.PAID);
                                    orderRepository.save(order);
                                    cartService.clearCart(order.getUser().getId());
                                    notificationService.sendOrderConfirmation(order);
                                }
                                throw new PaymentFailedException("Order has already been paid.");
                            }
                        }
                        return new PaymentIntentResponse(
                                existingPayment.getId().toString(),
                                intent.getId(),
                                syncedStatus,
                                existingPayment.getAmount(),
                                existingPayment.getCurrency(),
                                intent.getClientSecret()
                        );
                    }
                }
            }

            if (!stripeEnabled) {
                return createStubIntent(order, amount, currency);
            }

            // Convert to cents for Stripe
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency.toLowerCase())
                    .setDescription("Order #" + order.getOrderNumber())
                    .putMetadata("orderId", orderId.toString())
                    .putMetadata("orderNumber", order.getOrderNumber())
                    .setStatementDescriptor("ShopEase Purchase")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // Save payment record
            Payment payment = Payment.builder()
                    .order(order)
                    .provider("stripe")
                    .providerPaymentId(intent.getId())
                    .amount(amount)
                    .currency(currency)
                    .status(mapStripeStatus(intent.getStatus()))
                    .build();
            paymentRepository.save(payment);

            log.info("Created Stripe payment intent {} for order {}", intent.getId(), orderId);

            return new PaymentIntentResponse(
                    payment.getId().toString(),
                    intent.getId(),
                    mapStripeStatus(intent.getStatus()),
                    amount,
                    currency,
                    intent.getClientSecret()
            );
        } catch (StripeException e) {
            log.error("Failed to create Stripe payment intent: {}", e.getMessage());
            throw new PaymentFailedException("Failed to create payment intent: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatus confirm(String providerPaymentId) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "providerPaymentId", providerPaymentId));

        try {
            if (!stripeEnabled) {
                payment.setStatus(PaymentStatus.SUCCEEDED);
            } else {
                PaymentIntent intent = PaymentIntent.retrieve(providerPaymentId);
                payment.setStatus(mapStripeStatus(intent.getStatus()));
            }

            payment = paymentRepository.save(payment);

            // Update order status if payment succeeded
            if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
                Order order = payment.getOrder();
                if (order.getStatus() != OrderStatus.PAID) {
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                    cartService.clearCart(order.getUser().getId());
                    notificationService.sendOrderConfirmation(order);
                    log.info("Payment confirmed and order finalized for order {}", order.getOrderNumber());
                }
            }

            return payment.getStatus();
        } catch (StripeException e) {
            log.error("Failed to confirm Stripe payment: {}", e.getMessage());
            throw new PaymentFailedException("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Override
    public PaymentStatus handleWebhook(String provider, String providerPaymentId,
                                       String eventType, Map<String, Object> payload) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "providerPaymentId", providerPaymentId));

        PaymentStatus newStatus = switch (eventType) {
            case "payment_intent.succeeded" -> PaymentStatus.SUCCEEDED;
            case "payment_intent.payment_failed" -> PaymentStatus.FAILED;
            case "payment_intent.canceled" -> PaymentStatus.FAILED;
            default -> payment.getStatus();
        };

        if (newStatus != payment.getStatus()) {
            payment.setStatus(newStatus);
            payment = paymentRepository.save(payment);

            // Update order status if payment succeeded
            if (newStatus == PaymentStatus.SUCCEEDED) {
                Order order = payment.getOrder();
                if (order.getStatus() != OrderStatus.PAID) {
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                    cartService.clearCart(order.getUser().getId());
                    notificationService.sendOrderConfirmation(order);
                    log.info("Order {} marked as PAID and finalized via webhook", order.getOrderNumber());
                }
            }
        }

        return newStatus;
    }

    /**
     * Fallback to stubbed payment when Stripe is not configured.
     */
    private PaymentIntentResponse createStubIntent(Order order, BigDecimal amount, String currency) {
        String providerPaymentId = "pi_stub_" + System.nanoTime();

        Payment payment = Payment.builder()
                .order(order)
                .provider("stub")
                .providerPaymentId(providerPaymentId)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.warn("Using stubbed payment provider (Stripe not configured)");

        return new PaymentIntentResponse(
                payment.getId().toString(),
                providerPaymentId,
                PaymentStatus.PENDING,
                amount,
                currency,
                "stub_secret_" + providerPaymentId
        );
    }

    /**
     * Map Stripe payment status to our domain PaymentStatus.
     */
    private PaymentStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "processing" -> PaymentStatus.PENDING;
            case "requires_action", "requires_capture", "requires_confirmation", "requires_payment_method" -> PaymentStatus.PENDING;
            case "canceled" -> PaymentStatus.FAILED;
            default -> PaymentStatus.FAILED;
        };
    }
}
