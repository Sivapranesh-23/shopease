package com.shopease.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * App-wide configuration bound from {@code app.*} in application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        JwtProperties jwt,
        Cors cors,
        Pagination pagination,
        RateLimit rateLimit,
        Payment payment
) {
    public record Cors(List<String> allowedOrigins) {}

    public record Pagination(
            @Min(1) int defaultPageSize,
            @Min(1) int maxPageSize
    ) {}

    public record RateLimit(
            boolean enabled,
            @Min(1) int windowSeconds,
            @Min(1) int maxRequests
    ) {}

    public record Payment(
            @NotBlank String provider,
            String webhookSecret
    ) {}
}
