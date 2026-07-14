package com.shopease.service;

import com.shopease.dto.request.CategoryRequest;
import com.shopease.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    
    Page<CategoryResponse> listAll(Pageable pageable);
    
    CategoryResponse getById(Long id);
    
    CategoryResponse create(CategoryRequest request);
    
    CategoryResponse update(Long id, CategoryRequest request);
    
    void delete(Long id);
}
