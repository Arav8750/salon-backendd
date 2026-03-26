package com.salon.dto;

import com.salon.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class AppointmentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookRequest {
        private Long serviceId;
        private Long barberId;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentResponse {
        private Long id;
        private Long customerId;
        private String customerName;
        private Long barberId;
        private String barberName;
        private Long serviceId;
        private String serviceName;
        private Double servicePrice;
        private Integer serviceDuration;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private Integer tokenNumber;
        private String status;
        private Integer estimatedWaitMinutes;
        private String notes;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private String status;
    }
}
