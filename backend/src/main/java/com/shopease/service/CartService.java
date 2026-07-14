package com.shopease.service;

import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.response.CartResponse;

/**
 * Per-user shopping cart operations.
 */
public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, AddToCartRequest request);

    CartResponse updateItem(Long userId, Long productId, int quantity);

    CartResponse removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}
