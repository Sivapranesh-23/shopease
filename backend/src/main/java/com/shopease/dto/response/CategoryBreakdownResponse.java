package com.shopease.dto.response;

import java.math.BigDecimal;

/**
 * Share of revenue by category, for the admin breakdown chart.
 */
public record CategoryBreakdownResponse(
        String category,
        BigDecimal revenue,
        double percentage,
        long unitsSold
) {}
