package com.shopease.controller;

import com.shopease.dto.request.PlaceOrderRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.OrderResponse;
import com.shopease.security.SecurityUtils;
import com.shopease.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Place a new order from cart items")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        Long userId = securityUtils.requireCurrentUserId();
        OrderResponse response = orderService.placeOrder(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", response));
    }

    @Operation(summary = "Get a specific order by order number")
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable String orderNumber) {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderForUser(userId, orderNumber)));
    }

    @Operation(summary = "List the current user's orders")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> myOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                orderService.myOrders(userId, PageRequest.of(page, size))));
    }
}
