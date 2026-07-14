package com.shopease.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Elasticsearch repository for full-text product search.
 */
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
}
