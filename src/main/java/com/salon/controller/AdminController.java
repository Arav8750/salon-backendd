package com.salon.controller;

import com.salon.dto.*;
import com.salon.service.CustomerService;
import com.salon.service.DashboardService;
import com.salon.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ReportService reportService;

    // ── Dashboard ──────────────────────────────────────
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    // ── Customers ──────────────────────────────────────
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerDTO.CustomerResponse>>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO.CustomerResponse>> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PatchMapping("/customers/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.toggleCustomerStatus(id));
    }

    // ── Revenue Reports ────────────────────────────────
    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<ReportDTO.RevenueReport>> getRevenueReport(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(reportService.getRevenueReport(days));
    }
}
