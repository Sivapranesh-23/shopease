package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * Top-line numbers shown on the admin dashboard.
 */
public record DashboardSummaryResponse(
        BigDecimal totalRevenue,
        long totalOrders,
        long totalProducts,
        long totalUsers,
        long lowStockCount,
        BigDecimal averagePrice,
        BigDecimal averageRating,
        double conversionRate
) {}
