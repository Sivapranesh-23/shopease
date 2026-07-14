package com.shopease.service.impl;

import com.shopease.dto.request.CategoryRequest;
import com.shopease.domain.Category;
import com.shopease.dto.response.CategoryResponse;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.CategoryMapper;
import com.shopease.repository.CategoryRepository;
import com.shopease.service.CategoryService;
import com.shopease.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.validation.annotation.Validated;
import java.util.Objects;

/**
 * Implementation of CategoryService.
 * Provides CRUD operations for Category entities.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> listAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse create(@Validated CategoryRequest request) {
        // Generate slug from the supplied name
        String slug = SlugUtil.slugify(request.name());

        // Resolve optional parent category
        Category parent = (request.parentId() == null) ? null
                : categoryRepository.findById(request.parentId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Parent category with id " + request.parentId()
                                        + " does not exist"));

        // Build the new Category entity
        Category category = Category.builder()
                .name(request.name())          // use accessor method
                .slug(slug)
                .description(request.description())
                .imageUrl(request.imageUrl())
                .parent(parent)                 // set parent reference
                .build();

        // Persist and map to response DTO
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, @Validated CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));

        // Update scalar fields using accessor methods
        category.setName(request.name());
        category.setDescription(request.description());
        category.setImageUrl(request.imageUrl());

        // Handle optional parent update
        if (request.parentId() != null) {
            Long parentId = request.parentId();
            Category newParent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Parent category with id " + parentId + " does not exist"));
            category.setParent(newParent);
        } else if (request.parentId() == null && Objects.nonNull(category.getParent())) {
            // If client explicitly removes parent, detach it
            category.setParent(null);
        }

        // Persist changes and map to response DTO
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id.toString());
        }
        categoryRepository.deleteById(id);
    }
}