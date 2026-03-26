package com.salon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class ReportDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenue {
        private String date;
        private Double revenue;
        private Long completedAppointments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRevenue {
        private String serviceName;
        private Long count;
        private Double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueReport {
        private Double totalRevenue;
        private Long totalCompleted;
        private List<DailyRevenue> dailyBreakdown;
        private List<ServiceRevenue> serviceBreakdown;
    }
}
