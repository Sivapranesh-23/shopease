package com.shopease.exception;

import java.io.Serial;

/**
 * Thrown when a payment operation fails.
 */
public class PaymentFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentFailedException(String message) {
        super(message);
    }
}
