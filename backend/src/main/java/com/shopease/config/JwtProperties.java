package com.shopease.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * JWT configuration bound from {@code app.jwt.*} in application.yml.
 */
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

        @NotBlank
        String secret,

        @NotBlank
        String issuer,

        @Min(1)
        long accessTokenTtlMinutes,

        @Min(1)
        long refreshTokenTtlDays
) {
    public Duration accessTokenTtl() {
        return Duration.ofMinutes(accessTokenTtlMinutes);
    }

    public Duration refreshTokenTtl() {
        return Duration.ofDays(refreshTokenTtlDays);
    }
}
