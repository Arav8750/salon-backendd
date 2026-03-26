package com.salon.repository;

import com.salon.model.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    List<SalonService> findByIsActive(Boolean isActive);
}
