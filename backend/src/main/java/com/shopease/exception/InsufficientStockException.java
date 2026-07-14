package com.shopease.exception;

import java.io.Serial;

/**
 * Thrown when an operation would drive stock below zero.
 */
public class InsufficientStockException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}
