package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.ReportDTO;
import com.salon.model.Appointment;
import com.salon.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public ApiResponse<ReportDTO.RevenueReport> getRevenueReport(int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        // Get all completed appointments in range
        List<Appointment> allCompleted = appointmentRepository
                .findByStatus(Appointment.AppointmentStatus.COMPLETED)
                .stream()
                .filter(a -> !a.getAppointmentDate().isBefore(startDate))
                .collect(Collectors.toList());

        // Daily breakdown
        Map<LocalDate, List<Appointment>> byDate = allCompleted.stream()
                .collect(Collectors.groupingBy(Appointment::getAppointmentDate));

        List<ReportDTO.DailyRevenue> daily = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            List<Appointment> dayAppts = byDate.getOrDefault(d, Collections.emptyList());
            double rev = dayAppts.stream().mapToDouble(a -> a.getService().getPrice()).sum();
            daily.add(new ReportDTO.DailyRevenue(
                    d.format(DateTimeFormatter.ofPattern("dd MMM")),
                    rev,
                    (long) dayAppts.size()
            ));
        }

        // Service breakdown
        Map<String, List<Appointment>> byService = allCompleted.stream()
                .collect(Collectors.groupingBy(a -> a.getService().getName()));

        List<ReportDTO.ServiceRevenue> serviceBreakdown = byService.entrySet().stream()
                .map(e -> new ReportDTO.ServiceRevenue(
                        e.getKey(),
                        (long) e.getValue().size(),
                        e.getValue().stream().mapToDouble(a -> a.getService().getPrice()).sum()
                ))
                .sorted(Comparator.comparingDouble(ReportDTO.ServiceRevenue::getRevenue).reversed())
                .collect(Collectors.toList());

        double totalRevenue = allCompleted.stream().mapToDouble(a -> a.getService().getPrice()).sum();

        ReportDTO.RevenueReport report = new ReportDTO.RevenueReport(
                totalRevenue,
                (long) allCompleted.size(),
                daily,
                serviceBreakdown
        );

        return ApiResponse.success("Revenue report generated", report);
    }
}
