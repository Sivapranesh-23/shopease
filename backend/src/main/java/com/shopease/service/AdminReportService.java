package com.shopease.service;

import com.shopease.dto.response.CategoryBreakdownResponse;
import com.shopease.dto.response.DashboardSummaryResponse;
import com.shopease.dto.response.RevenuePointResponse;

import java.util.List;

/**
 * Aggregated dashboard metrics for admin.
 */
public interface AdminReportService {

    DashboardSummaryResponse getSummary();

    List<RevenuePointResponse> getRevenueByMonth(int months);

    List<CategoryBreakdownResponse> getCategoryBreakdown();
}
