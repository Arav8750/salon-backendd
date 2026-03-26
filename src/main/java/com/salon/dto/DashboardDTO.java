package com.salon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Long totalAppointmentsToday;
    private Long completedToday;
    private Long waitingCount;
    private Long inProgressCount;
    private Double revenueToday;
    private Long totalCustomers;
    private Long totalBarbers;
    private Long totalServices;
}
