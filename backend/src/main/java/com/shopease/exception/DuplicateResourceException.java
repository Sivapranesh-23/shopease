package com.shopease.exception;

import java.io.Serial;

/**
 * Thrown when a create/update would violate a unique constraint.
 */
public class DuplicateResourceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) {
        super(message);
    }
}
