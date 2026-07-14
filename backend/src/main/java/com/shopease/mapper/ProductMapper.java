package com.shopease.mapper;

import com.shopease.domain.Product;
import com.shopease.dto.request.StoreProductRequest;
import com.shopease.dto.response.ProductResponse;
import com.shopease.repository.search.ProductDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId",    source = "category.id")
    @Mapping(target = "categoryName",  source = "category.name")
    @Mapping(target = "categorySlug",  source = "category.slug")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "rating", constant = "0.0")
    @Mapping(target = "reviewCount", constant = "0")
    @Mapping(target = "compareAtPrice", source = "compareAtPrice")
    Product toEntity(StoreProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(StoreProductRequest request, @MappingTarget Product product);

    @Named("toDocument")
    @Mapping(target = "categorySlug", source = "category.slug")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDocument toDocument(Product product);
}
