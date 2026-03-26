package com.salon.repository;

import com.salon.model.Appointment;
import com.salon.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerId(Long customerId);
    List<Appointment> findByBarberId(Long barberId);
    List<Appointment> findByAppointmentDate(LocalDate date);
    List<Appointment> findByBarberIdAndAppointmentDate(Long barberId, LocalDate date);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    List<Appointment> findByAppointmentDateAndStatus(LocalDate date, Appointment.AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.barber.id = :barberId AND a.appointmentDate = :date AND a.status IN ('WAITING', 'IN_PROGRESS')")
    Long countActiveByBarberAndDate(@Param("barberId") Long barberId, @Param("date") LocalDate date);

    @Query("SELECT MAX(a.tokenNumber) FROM Appointment a WHERE a.appointmentDate = :date")
    Integer findMaxTokenForDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.status = 'COMPLETED'")
    Long countCompletedByDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(s.price) FROM Appointment a JOIN a.service s WHERE a.appointmentDate = :date AND a.status = 'COMPLETED'")
    Double sumRevenueByDate(@Param("date") LocalDate date);

    List<Appointment> findByBarberIdAndStatus(Long barberId, Appointment.AppointmentStatus status);

    // --- AI Insights queries ---
    @Query("SELECT a.service.name, COUNT(a) as cnt FROM Appointment a GROUP BY a.service.name ORDER BY cnt DESC")
    List<Object[]> findServicePopularity();

    @Query("SELECT a.barber.user.name, COUNT(a) as cnt FROM Appointment a WHERE a.barber IS NOT NULL GROUP BY a.barber.user.name ORDER BY cnt DESC")
    List<Object[]> findBarberPerformance();

    @Query("SELECT DAYNAME(a.appointmentDate), COUNT(a) as cnt FROM Appointment a GROUP BY DAYNAME(a.appointmentDate) ORDER BY cnt DESC")
    List<Object[]> findBusiestDay();
}
