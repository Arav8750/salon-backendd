package com.salon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BarberDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String specialization;
        private Integer experienceYears;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BarberResponse {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private String specialization;
        private Integer experienceYears;
        private String status;
        private Boolean isActive;
    }
}
