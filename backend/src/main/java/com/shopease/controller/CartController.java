package com.shopease.controller;

import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.CartResponse;
import com.shopease.security.SecurityUtils;
import com.shopease.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Get the current user's cart")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId)));
    }

    @Operation(summary = "Add an item to the cart")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddToCartRequest request) {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(cartService.addItem(userId, request)));
    }

    @Operation(summary = "Update cart item quantity")
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(cartService.updateItem(userId, productId, quantity)));
    }

    @Operation(summary = "Remove an item from the cart")
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long productId) {
        Long userId = securityUtils.requireCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(userId, productId)));
    }

    @Operation(summary = "Clear the entire cart")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {
        Long userId = securityUtils.requireCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.message("Cart cleared"));
    }
}
