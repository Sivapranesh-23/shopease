package com.shopease.domain;

import com.shopease.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A confirmed customer order. Immutable line items live in {@link OrderItem}.
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-friendly order reference, e.g. {@code LX-9042}. */
    @Column(name = "order_number", nullable = false, unique = true, length = 24)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "shipping_total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingTotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal;

    /** Flattened shipping address snapshot. */
    @Column(name = "shipping_address", length = 600)
    private String shippingAddress;

    @Column(name = "shipping_carrier", length = 60)
    private String shippingCarrier;

    @Column(name = "tracking_number", length = 80)
    private String trackingNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
