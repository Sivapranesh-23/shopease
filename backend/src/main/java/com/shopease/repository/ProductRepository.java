package com.shopease.repository;

import com.shopease.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByActiveTrueAndPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String keyword, Pageable pageable);

    List<Product> findByActiveTrueAndStockLessThan(Integer threshold);

    long countByActiveTrue();

    @Query("""
            SELECT AVG(p.rating) FROM Product p WHERE p.active = true
            """)
    Double averageRating();

    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            ORDER BY p.rating DESC
            """)
    List<Product> findTopRated(Pageable pageable);
}
