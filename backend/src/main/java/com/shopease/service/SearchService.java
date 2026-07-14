package com.shopease.service;

import com.shopease.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Elasticsearch-powered full-text product search.
 */
public interface SearchService {

    Page<ProductResponse> search(String query, Pageable pageable);

    void indexProduct(Long productId);

    void indexAll();
}
