package com.shopease.domain;

import com.shopease.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Payment record for an order. Tracks provider, status and any provider-specific reference.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    /** Provider identifier, e.g. {@code stripe}, {@code paypal}, {@code stub}. */
    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    /** Provider-side payment reference / charge id. */
    @Column(name = "provider_payment_id", length = 120)
    private String providerPaymentId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(length = 400)
    private String metadata; // JSON string for flexible provider-specific fields
}
