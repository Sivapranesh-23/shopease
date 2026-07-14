package com.shopease.controller;

import com.shopease.domain.Product;
import com.shopease.domain.User;
import com.shopease.domain.WishlistItem;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.ProductMapper;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.repository.WishlistItemRepository;
import com.shopease.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Wishlist")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Transactional
public class WishlistController {

    private final WishlistItemRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Get the current user's wishlist")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlist() {
        Long userId = securityUtils.requireCurrentUserId();
        List<ProductResponse> products = wishlistRepository.findByUserId(userId).stream()
                .filter(java.util.Objects::nonNull)
                .map(item -> item.getProduct())
                .filter(java.util.Objects::nonNull)
                .map(product -> productMapper.toResponse(product))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @Operation(summary = "Add a product to the wishlist")
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> addToWishlist(@PathVariable Long productId) {
        Long userId = securityUtils.requireCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        WishlistItem item = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseGet(() -> wishlistRepository.save(WishlistItem.builder()
                        .user(user)
                        .product(product)
                        .build()));

        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", productMapper.toResponse(item.getProduct())));
    }

    @Operation(summary = "Remove a product from the wishlist")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(@PathVariable Long productId) {
        Long userId = securityUtils.requireCurrentUserId();
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ApiResponse.message("Product removed from wishlist"));
    }
}
