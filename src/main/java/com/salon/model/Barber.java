package com.salon.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "barbers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private String specialization;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BarberStatus status = BarberStatus.AVAILABLE;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum BarberStatus {
        AVAILABLE, BUSY, OFF_DUTY
    }
}
