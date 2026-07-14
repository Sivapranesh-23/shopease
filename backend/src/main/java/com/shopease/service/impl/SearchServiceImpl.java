package com.shopease.service.impl;

import com.shopease.domain.Product;
import com.shopease.dto.response.ProductResponse;
import com.shopease.mapper.ProductMapper;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.search.ProductDocument;
import com.shopease.repository.search.ProductSearchRepository;
import com.shopease.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductMapper productMapper;

    public SearchServiceImpl(ProductRepository productRepository,
                             ProductSearchRepository searchRepository,
                             ElasticsearchOperations elasticsearchOperations,
                             ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponse> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            log.debug("Empty search query, returning empty results");
            return Page.empty(pageable);
        }

        try {
            String sanitizedQuery = query.trim().toLowerCase();
            co.elastic.clients.elasticsearch._types.query_dsl.Query nameQuery = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(sq -> sq
                    .match(m -> m.field("name").query(sanitizedQuery).boost(2.0f)));
            co.elastic.clients.elasticsearch._types.query_dsl.Query summaryQuery = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(sq -> sq
                    .match(m -> m.field("summary").query(sanitizedQuery).boost(1.5f)));
            co.elastic.clients.elasticsearch._types.query_dsl.Query descriptionQuery = co.elastic.clients.elasticsearch._types.query_dsl.Query.of(sq -> sq
                    .match(m -> m.field("description").query(sanitizedQuery)));

            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .bool(b -> b
                                    .should(nameQuery, summaryQuery, descriptionQuery)
                                    .filter(f -> f.term(t -> t.field("active").value(true)))
                            )
                    )
                    .withPageable(pageable)
                    .build();

            SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);
            List<ProductResponse> content = hits.getSearchHits().stream()
                    .filter(java.util.Objects::nonNull)
                    .map(hit -> hit.getContent())
                    .map(doc -> {
                        if (doc == null || doc.getId() == null) {
                            return null;
                        }
                        return productRepository.findById(doc.getId())
                                .map(productMapper::toResponse)
                                .orElse(null);
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();

            log.debug("Elasticsearch search for '{}' returned {} results", sanitizedQuery, content.size());
            return new org.springframework.data.domain.PageImpl<>(content, pageable, hits.getTotalHits());
        } catch (Exception e) {
            log.warn("Elasticsearch search failed for query '{}', falling back to database search: {}", query, e.getMessage());
            return fallbackSearch(query, pageable);
        }
    }

    /**
     * Fallback to database search when Elasticsearch is unavailable.
     */
    private Page<ProductResponse> fallbackSearch(String query, Pageable pageable) {
        String searchTerm = "%" + query.trim().toLowerCase() + "%";
        return productRepository.findAll((root, ignoredQuery, cb) -> cb.and(
                cb.isTrue(root.get("active")),
                cb.or(
                        cb.like(cb.lower(root.get("name")), searchTerm),
                        cb.like(cb.lower(root.get("summary")), searchTerm),
                        cb.like(cb.lower(root.get("description")), searchTerm)
                )
        ), pageable).map(productMapper::toResponse);
    }

    @Override
    public void indexProduct(Long productId) {
        try {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                ProductDocument doc = productMapper.toDocument(product);
                searchRepository.save(doc);
            }
        } catch (Exception e) {
            log.warn("Failed to index product {}: {}", productId, e.getMessage());
        }
    }

    @Override
    public void indexAll() {
        try {
            List<Product> products = productRepository.findAll();
            List<ProductDocument> docs = products.stream()
                    .map(productMapper::toDocument)
                    .toList();
            searchRepository.saveAll(docs);
            log.info("Indexed {} products to Elasticsearch", docs.size());
        } catch (Exception e) {
            log.warn("Bulk index failed: {}", e.getMessage());
        }
    }
}
