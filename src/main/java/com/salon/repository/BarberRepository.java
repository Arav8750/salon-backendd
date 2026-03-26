package com.salon.repository;

import com.salon.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findByIsActive(Boolean isActive);
    List<Barber> findByStatus(Barber.BarberStatus status);
    Optional<Barber> findByUserId(Long userId);
    List<Barber> findByStatusAndIsActive(Barber.BarberStatus status, Boolean isActive);
}
