package com.shopease.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * A wishlisted product for a customer.
 */
@Entity
@Table(name = "wishlist_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wishlist_items_user_product",
                columnNames = {"user_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
