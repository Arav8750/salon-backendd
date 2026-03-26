package com.salon.repository;

import com.salon.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByAppointmentId(Long appointmentId);

    @Query("SELECT MAX(t.tokenNumber) FROM Token t WHERE t.tokenDate = :date")
    Integer findMaxTokenNumberForDate(@Param("date") LocalDate date);
}
