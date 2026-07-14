package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * A single monthly data point on the revenue growth chart.
 */
public record RevenuePointResponse(
        String label,
        BigDecimal revenue,
        long orderCount
) {}
