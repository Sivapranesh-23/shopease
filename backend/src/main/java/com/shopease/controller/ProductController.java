package com.shopease.controller;

import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.CategoryResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.mapper.CategoryMapper;
import com.shopease.repository.CategoryRepository;
import com.shopease.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // --- Products ---

    @Operation(summary = "List active products (paginated)")
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(productService.listActive(pageable)));
    }

    @Operation(summary = "Search products with filters")
    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                productService.search(q, category, minPrice, maxPrice, sort, pageable)));
    }

    @Operation(summary = "Get product by slug")
    @GetMapping("/products/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug)));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/products/id/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @Operation(summary = "Get related products")
    @GetMapping("/products/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> related(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(ApiResponse.success(productService.related(id, limit)));
    }

    // --- Categories ---

    @Operation(summary = "List all categories")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> categories() {
        return ResponseEntity.ok(ApiResponse.success(
                categoryRepository.findAllWithChildren().stream().map(categoryMapper::toResponse).toList()));
    }

    @Operation(summary = "Get category by slug")
    @GetMapping("/categories/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> category(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
                categoryRepository.findBySlug(slug).map(categoryMapper::toResponse).orElseThrow()));
    }
}
