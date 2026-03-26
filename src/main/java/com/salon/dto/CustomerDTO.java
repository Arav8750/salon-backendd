package com.salon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class CustomerDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private Long totalAppointments;
    }
}
