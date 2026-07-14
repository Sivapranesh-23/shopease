package com.shopease.service;

import com.shopease.domain.enums.OrderStatus;
import com.shopease.dto.request.PlaceOrderRequest;
import com.shopease.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Order lifecycle operations for customers and admins.
 */
public interface OrderService {

    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    @org.springframework.lang.NonNull
    OrderResponse getOrderForUser(Long userId, String orderNumber);

    Page<OrderResponse> myOrders(Long userId, Pageable pageable);

    // --- Admin ---

    Page<OrderResponse> adminList(OrderStatus status, Pageable pageable);

    Page<OrderResponse> listAllOrders(Pageable pageable);

    OrderResponse adminGet(Long orderId);

    OrderResponse getOrderDetails(Long orderId);

    OrderResponse updateStatus(Long orderId, OrderStatus status);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    OrderResponse assignTracking(Long orderId, String carrier, String trackingNumber);

    OrderResponse cancelOrder(Long orderId);
}
