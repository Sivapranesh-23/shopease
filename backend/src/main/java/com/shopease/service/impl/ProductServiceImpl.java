package com.shopease.service.impl;

import com.shopease.domain.Category;
import com.shopease.domain.Product;
import com.shopease.dto.request.StoreProductRequest;
import com.shopease.dto.request.UpdateProductRequest;
import com.shopease.dto.response.ProductResponse;
import com.shopease.exception.DuplicateResourceException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.ProductMapper;
import com.shopease.repository.CategoryRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.search.ProductDocument;
import com.shopease.repository.search.ProductSearchRepository;
import com.shopease.service.ProductService;
import com.shopease.util.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductSearchRepository searchRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.searchRepository = searchRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> listActive(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Long categoryId, BigDecimal minPrice,
                                        BigDecimal maxPrice, String sort, Pageable pageable) {
        Pageable sorted = withSort(pageable, sort);
        Specification<Product> spec = (root, ignoredQuery, cb) -> cb.isTrue(root.get("active"));
        if (StringUtils.hasText(query)) {
            String term = "%" + query.trim().toLowerCase() + "%";
            spec = spec.and((root, ignoredQuery, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), term),
                    cb.like(cb.lower(root.get("summary")), term),
                    cb.like(cb.lower(root.get("description")), term)));
        }
        if (categoryId != null) {
            spec = spec.and((root, ignoredQuery, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (minPrice != null) {
            spec = spec.and((root, ignoredQuery, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, ignoredQuery, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        return productRepository.findAll(spec, sorted).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> related(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Page<Product> page = productRepository.findByActiveTrueAndCategoryId(
                product.getCategory().getId(),
                PageRequest.of(0, limit + 1, Sort.by(Sort.Direction.DESC, "rating")));
        return page.stream()
                .filter(p -> !p.getId().equals(productId))
                .limit(limit)
                .map(productMapper::toResponse)
                .toList();
    }

    // --- Admin ---

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> listAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Override
    public ProductResponse create(StoreProductRequest request) {
        if (productRepository.findBySku(request.sku()).isPresent()) {
            throw new DuplicateResourceException("A product with SKU " + request.sku() + " already exists");
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));

        Product product = productMapper.toEntity(request);
        product.setSlug(SlugUtil.slugify(request.name() + "-" + request.sku()));
        product.setCategory(category);
        if (request.active() != null) {
            product.setActive(request.active());
        } else {
            product.setActive(true);
        }
        product = productRepository.save(product);
        index(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (StringUtils.hasText(request.name())) product.setName(request.name());
        if (StringUtils.hasText(request.sku())) {
            if (!request.sku().equals(product.getSku())
                    && productRepository.findBySku(request.sku()).isPresent()) {
                throw new DuplicateResourceException("A product with SKU " + request.sku() + " already exists");
            }
            product.setSku(request.sku());
        }
        if (request.summary() != null) product.setSummary(request.summary());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.compareAtPrice() != null) product.setCompareAtPrice(request.compareAtPrice());
        if (request.stock() != null) product.setStock(request.stock());
        if (request.imageUrl() != null) product.setImageUrl(request.imageUrl());
        if (request.active() != null) product.setActive(request.active());
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
            product.setCategory(category);
        }
        if (StringUtils.hasText(request.name())) {
            product.setSlug(SlugUtil.slugify(request.name() + "-" + product.getSku()));
        }
        product = productRepository.save(product);
        index(product);
        return productMapper.toResponse(product);
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
        searchRepository.deleteById(id);
    }

    @Override
    public void bulkUpdateStock(java.util.Map<Long, Integer> stockUpdates) {
        stockUpdates.forEach((productId, newStock) -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            product.setStock(newStock);
            productRepository.save(product);
        });
    }

    private void index(Product product) {
        try {
            ProductDocument doc = productMapper.toDocument(product);
            searchRepository.save(doc);
        } catch (Exception ignored) {
            // Search index is best-effort; the DB is the source of truth.
        }
    }

    private Pageable withSort(Pageable pageable, String sort) {
        if (!StringUtils.hasText(sort)) return pageable;
        return switch (sort.toLowerCase().replace('-', '_')) {
            case "price_asc" -> PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("price").ascending());
            case "price_desc" -> PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("price").descending());
            case "rating" -> PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("rating").descending());
            case "newest" -> PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("createdAt").descending());
            default -> pageable;
        };
    }
}
