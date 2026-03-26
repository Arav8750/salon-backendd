package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.AppointmentDTO;
import com.salon.exception.ResourceNotFoundException;
import com.salon.model.*;
import com.salon.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BarberRepository barberRepository;
    @Autowired private SalonServiceRepository salonServiceRepository;
    @Autowired private TokenRepository tokenRepository;

    public ApiResponse<AppointmentDTO.AppointmentResponse> bookAppointment(Long customerId, AppointmentDTO.BookRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        SalonService service = salonServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Barber barber = null;
        if (request.getBarberId() != null) {
            barber = barberRepository.findById(request.getBarberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Barber not found"));
        } else {
            // Auto-assign available barber
            List<Barber> available = barberRepository.findByStatusAndIsActive(Barber.BarberStatus.AVAILABLE, true);
            if (!available.isEmpty()) barber = available.get(0);
        }

        // Generate token number
        Integer lastToken = appointmentRepository.findMaxTokenForDate(request.getAppointmentDate());
        int tokenNumber = (lastToken == null) ? 1 : lastToken + 1;

        // Estimate waiting time
        int queueSize = (barber != null)
                ? appointmentRepository.countActiveByBarberAndDate(barber.getId(), request.getAppointmentDate()).intValue()
                : 0;
        int estimatedWait = queueSize * service.getDurationMinutes();

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setBarber(barber);
        appointment.setService(service);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setTokenNumber(tokenNumber);
        appointment.setStatus(Appointment.AppointmentStatus.WAITING);
        appointment.setEstimatedWaitMinutes(estimatedWait);
        appointment.setNotes(request.getNotes());
        appointmentRepository.save(appointment);

        // Save token
        Token token = new Token();
        token.setTokenNumber(tokenNumber);
        token.setTokenDate(request.getAppointmentDate());
        token.setAppointment(appointment);
        tokenRepository.save(token);

        return ApiResponse.success("Appointment booked successfully", toResponse(appointment));
    }

    public ApiResponse<List<AppointmentDTO.AppointmentResponse>> getCustomerAppointments(Long customerId) {
        List<Appointment> appointments = appointmentRepository.findByCustomerId(customerId);
        return ApiResponse.success("Appointments fetched", appointments.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<List<AppointmentDTO.AppointmentResponse>> getBarberAppointments(Long userId) {
        Barber barber = barberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber profile not found"));
        List<Appointment> appointments = appointmentRepository.findByBarberId(barber.getId());
        return ApiResponse.success("Appointments fetched", appointments.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<List<AppointmentDTO.AppointmentResponse>> getAllAppointments() {
        return ApiResponse.success("All appointments", appointmentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<List<AppointmentDTO.AppointmentResponse>> getTodayAppointments() {
        return ApiResponse.success("Today's appointments",
                appointmentRepository.findByAppointmentDate(LocalDate.now())
                        .stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<AppointmentDTO.AppointmentResponse> updateStatus(Long id, String status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(status));
        appointmentRepository.save(appointment);
        return ApiResponse.success("Status updated", toResponse(appointment));
    }

    public ApiResponse<AppointmentDTO.AppointmentResponse> cancelAppointment(Long id, Long userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!appointment.getCustomer().getId().equals(userId)) {
            return ApiResponse.error("Unauthorized");
        }
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return ApiResponse.success("Appointment cancelled", toResponse(appointment));
    }

    private AppointmentDTO.AppointmentResponse toResponse(Appointment a) {
        return new AppointmentDTO.AppointmentResponse(
                a.getId(),
                a.getCustomer().getId(),
                a.getCustomer().getName(),
                a.getBarber() != null ? a.getBarber().getId() : null,
                a.getBarber() != null ? a.getBarber().getUser().getName() : "Not Assigned",
                a.getService().getId(),
                a.getService().getName(),
                a.getService().getPrice(),
                a.getService().getDurationMinutes(),
                a.getAppointmentDate(),
                a.getAppointmentTime(),
                a.getTokenNumber(),
                a.getStatus().name(),
                a.getEstimatedWaitMinutes(),
                a.getNotes(),
                a.getCreatedAt()
        );
    }
}
