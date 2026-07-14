package com.shopease.controller.admin;

import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.CategoryBreakdownResponse;
import com.shopease.dto.response.DashboardSummaryResponse;
import com.shopease.dto.response.RevenuePointResponse;
import com.shopease.service.AdminReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin — Reports")
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService reportService;

    @Operation(summary = "Get dashboard summary metrics")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> summary() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSummary()));
    }

    @Operation(summary = "Get monthly revenue history")
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<RevenuePointResponse>>> revenue(
            @RequestParam(defaultValue = "6") int months) {
        int boundedMonths = Math.max(1, Math.min(months, 24));
        return ResponseEntity.ok(ApiResponse.success(reportService.getRevenueByMonth(boundedMonths)));
    }

    @Operation(summary = "Get product category breakdown")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryBreakdownResponse>>> categories() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getCategoryBreakdown()));
    }
}
