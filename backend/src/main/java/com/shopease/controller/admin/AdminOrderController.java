package com.shopease.controller.admin;

import com.shopease.domain.enums.OrderStatus;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.OrderResponse;
import com.shopease.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Admin — Orders")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "List orders (admin, optionally filtered by status)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.adminList(status, PageRequest.of(page, size))));
    }

    @Operation(summary = "Get order by ID (admin)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.adminGet(id)));
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        OrderStatus status = OrderStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(ApiResponse.success(orderService.updateStatus(id, status)));
    }

    @Operation(summary = "Assign tracking info to an order")
    @PostMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<OrderResponse>> assignTracking(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.assignTracking(id, body.get("carrier"), body.get("trackingNumber"))));
    }
}
