package com.shopease.exception;

import java.io.Serial;

/**
 * Thrown when a non-admin attempts an admin-protected action.
 */
public class UnauthorisedAdminException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorisedAdminException() {
        super("Access denied. Admin privileges required.");
    }
}
