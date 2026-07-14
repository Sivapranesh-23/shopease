package com.shopease.service;

import com.shopease.dto.request.StoreProductRequest;
import com.shopease.dto.request.UpdateProductRequest;
import com.shopease.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Product catalogue operations (storefront read + admin write).
 */
public interface ProductService {

    Page<ProductResponse> listActive(Pageable pageable);

    Page<ProductResponse> search(String query, Long categoryId, BigDecimal minPrice,
                                 BigDecimal maxPrice, String sort, Pageable pageable);

    ProductResponse getBySlug(String slug);

    ProductResponse getById(Long id);

    List<ProductResponse> related(Long productId, int limit);

    // --- Admin ---

    Page<ProductResponse> listAll(Pageable pageable);

    ProductResponse create(StoreProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void delete(Long id);

    void bulkUpdateStock(Map<Long, Integer> stockUpdates);
}
