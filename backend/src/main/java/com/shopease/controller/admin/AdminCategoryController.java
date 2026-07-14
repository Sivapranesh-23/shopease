package com.shopease.controller.admin;

import com.shopease.domain.Category;
import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.CategoryResponse;
import com.shopease.exception.DuplicateResourceException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.CategoryMapper;
import com.shopease.repository.CategoryRepository;
import com.shopease.util.SlugUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin — Categories")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success(
                categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList()));
    }

    @Operation(summary = "Create a category")
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryPayload payload) {
        String slug = uniqueSlug(payload.name(), null);
        Category category = Category.builder()
                .name(payload.name().trim())
                .slug(slug)
                .description(payload.description())
                .imageUrl(payload.imageUrl())
                .parent(resolveParent(payload.parentId()))
                .build();
        return ResponseEntity.ok(ApiResponse.success("Category created",
                categoryMapper.toResponse(categoryRepository.save(category))));
    }

    @Operation(summary = "Update a category")
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CategoryPayload payload) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (id.equals(payload.parentId())) {
            throw new IllegalArgumentException("A category cannot be its own parent");
        }
        category.setName(payload.name().trim());
        category.setSlug(uniqueSlug(payload.name(), id));
        category.setDescription(payload.description());
        category.setImageUrl(payload.imageUrl());
        category.setParent(resolveParent(payload.parentId()));
        return ResponseEntity.ok(ApiResponse.success("Category updated",
                categoryMapper.toResponse(categoryRepository.save(category))));
    }

    @Operation(summary = "Delete a category")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("Move or delete child categories first");
        }
        categoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.message("Category deleted"));
    }

    private Category resolveParent(Long parentId) {
        if (parentId == null) return null;
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category", "id", parentId));
    }

    private String uniqueSlug(String name, Long currentId) {
        String slug = SlugUtil.slugify(name);
        categoryRepository.findBySlug(slug).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new DuplicateResourceException("A category with this name already exists");
            }
        });
        return slug;
    }

    public record CategoryPayload(
            @NotBlank @Size(max = 100) String name,
            @Size(max = 280) String description,
            @Size(max = 600) String imageUrl,
            Long parentId
    ) {}
}
