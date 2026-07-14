package com.shopease.repository;

import com.shopease.domain.Order;
import com.shopease.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.grandTotal), 0) FROM Order o WHERE o.status NOT IN ('PENDING', 'CANCELLED', 'REFUNDED')")
    BigDecimal totalRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    long countAll();

    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findRecentByStatuses(@Param("statuses") List<OrderStatus> statuses, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    List<Order> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(Instant start, Instant end);
}
