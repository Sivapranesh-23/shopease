package com.shopease.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe API configuration. Initializes Stripe secret key on application startup.
 */
@Configuration
public class StripeConfig {

    public StripeConfig(@Value("${app.payment.stripe-secret-key:}") String stripeSecretKey) {
        if (stripeSecretKey != null && !stripeSecretKey.isEmpty()) {
            Stripe.apiKey = stripeSecretKey;
        }
    }
}
