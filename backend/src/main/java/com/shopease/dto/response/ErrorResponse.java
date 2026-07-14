package com.shopease.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Standard error payload returned by the global exception handler.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors,
        List<String> details
) {}
