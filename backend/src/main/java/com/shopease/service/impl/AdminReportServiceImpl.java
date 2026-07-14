package com.shopease.service.impl;

import com.shopease.domain.Category;
import com.shopease.domain.Product;
import com.shopease.domain.enums.OrderStatus;
import com.shopease.dto.response.CategoryBreakdownResponse;
import com.shopease.dto.response.DashboardSummaryResponse;
import com.shopease.dto.response.RevenuePointResponse;
import com.shopease.repository.CategoryRepository;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.AdminReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminReportServiceImpl implements AdminReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AdminReportServiceImpl(OrderRepository orderRepository,
                                 ProductRepository productRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalRevenue = orderRepository.totalRevenue();
        long totalOrders = orderRepository.countAll();
        long totalProducts = productRepository.countByActiveTrue();
        long totalUsers = userRepository.count();
        long lowStock = productRepository.findByActiveTrueAndStockLessThan(10).size();

        BigDecimal avgPrice = productRepository.findAll().stream()
                .filter(java.util.Objects::nonNull)
                .map(product -> product.getPrice())
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        long productCount = productRepository.count();
        BigDecimal avgPriceVal = productCount > 0
                ? avgPrice.divide(BigDecimal.valueOf(productCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Double rating = productRepository.averageRating();
        BigDecimal avgRating = rating == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP);

        return new DashboardSummaryResponse(
                totalRevenue, totalOrders, totalProducts, totalUsers,
                lowStock, avgPriceVal, avgRating, 0.0);
    }

    @Override
    public List<RevenuePointResponse> getRevenueByMonth(int months) {
        List<RevenuePointResponse> data = new ArrayList<>();
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1);
            Instant start = monthStart.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant end = monthEnd.atStartOfDay().toInstant(ZoneOffset.UTC);

            var orders = orderRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(start, end)
                    .stream()
                    .filter(order -> order.getStatus() != OrderStatus.CANCELLED
                            && order.getStatus() != OrderStatus.REFUNDED)
                    .toList();
            BigDecimal revenue = orders.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(order -> order.getGrandTotal())
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
            data.add(new RevenuePointResponse(
                    monthStart.format(fmt),
                    revenue,
                    orders.size()
            ));
        }
        return data;
    }

    @Override
    public List<CategoryBreakdownResponse> getCategoryBreakdown() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryBreakdownResponse> breakdown = new ArrayList<>();

        long totalProducts = productRepository.countByActiveTrue();
        for (Category category : categories) {
            long count = productRepository.findByActiveTrueAndCategoryId(category.getId(),
                    org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
            double percentage = totalProducts > 0 ? (double) count / totalProducts * 100 : 0;
            breakdown.add(new CategoryBreakdownResponse(
                    category.getName(),
                    BigDecimal.ZERO,
                    Math.round(percentage * 10.0) / 10.0,
                    count
            ));
        }
        return breakdown;
    }
}
