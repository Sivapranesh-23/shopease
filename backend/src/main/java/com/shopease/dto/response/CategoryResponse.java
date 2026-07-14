package com.shopease.dto.response;

/**
 * Public category representation.
 */
public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long parentId
) {}
