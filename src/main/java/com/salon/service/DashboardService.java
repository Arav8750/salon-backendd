package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.DashboardDTO;
import com.salon.model.Appointment;
import com.salon.model.User;
import com.salon.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BarberRepository barberRepository;
    @Autowired private SalonServiceRepository salonServiceRepository;

    public ApiResponse<DashboardDTO> getAdminDashboard() {
        LocalDate today = LocalDate.now();

        List<Appointment> todayAppts = appointmentRepository.findByAppointmentDate(today);
        long completed = todayAppts.stream().filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED).count();
        long waiting = todayAppts.stream().filter(a -> a.getStatus() == Appointment.AppointmentStatus.WAITING).count();
        long inProgress = todayAppts.stream().filter(a -> a.getStatus() == Appointment.AppointmentStatus.IN_PROGRESS).count();

        Double revenue = appointmentRepository.sumRevenueByDate(today);

        DashboardDTO dto = new DashboardDTO(
                (long) todayAppts.size(),
                completed,
                waiting,
                inProgress,
                revenue != null ? revenue : 0.0,
                (long) userRepository.findByRole(User.Role.CUSTOMER).size(),
                (long) barberRepository.findByIsActive(true).size(),
                (long) salonServiceRepository.findByIsActive(true).size()
        );

        return ApiResponse.success("Dashboard data fetched", dto);
    }
}
