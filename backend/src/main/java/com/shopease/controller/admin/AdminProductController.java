package com.shopease.controller.admin;

import com.shopease.dto.request.StoreProductRequest;
import com.shopease.dto.request.UpdateProductRequest;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.ProductResponse;
import com.shopease.service.ProductService;
import com.shopease.mapper.ProductMapper;
import com.shopease.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Admin — Products")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Operation(summary = "List all products (admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(
                productRepository.findAll(pageable).map(productMapper::toResponse)));
    }

    @Operation(summary = "Get product by ID (admin)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody StoreProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.create(request)));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.update(id, request)));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Product deleted"));
    }

    @Operation(summary = "Bulk update product stock")
    @PatchMapping("/stock/bulk")
    public ResponseEntity<ApiResponse<String>> bulkUpdateStock(@RequestBody Map<Long, Integer> stockUpdates) {
        productService.bulkUpdateStock(stockUpdates);
        return ResponseEntity.ok(ApiResponse.success("Stock updated for " + stockUpdates.size() + " products"));
    }
}
