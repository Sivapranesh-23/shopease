package com.shopease.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * Checkout payload: converts the current cart into a confirmed order.
 */
public record PlaceOrderRequest(

        @NotNull(message = "Shipping address is required")
        @Valid
        AddressPayload shippingAddress,

        /** Optional — if omitted, payment is created in PENDING state. */
        PaymentPayload payment,

        @NotEmpty(message = "Cart cannot be empty")
        @Size(max = 50, message = "An order cannot contain more than 50 line items")
        @Valid
        List<CartItemPayload> items
) {
    public record AddressPayload(
            @NotBlank String fullName,
            @NotBlank String phone,
            @NotBlank String line1,
            String line2,
            @NotBlank String city,
            @NotBlank String postalCode,
            @NotBlank String country
    ) {}

    public record PaymentPayload(
            /** Provider token/intent from the frontend (e.g. Stripe payment method). */
            String paymentMethodId
    ) {}

    public record CartItemPayload(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {}
}
