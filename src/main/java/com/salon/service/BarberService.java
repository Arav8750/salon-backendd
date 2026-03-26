package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.BarberDTO;
import com.salon.exception.ResourceNotFoundException;
import com.salon.model.Barber;
import com.salon.model.User;
import com.salon.repository.BarberRepository;
import com.salon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarberService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse<BarberDTO.BarberResponse> getBarberByUserId(Long userId) {
        Barber barber = barberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Barber profile not found for user: " + userId));
        return ApiResponse.success("Barber profile fetched", toResponse(barber));
    }

    public ApiResponse<List<BarberDTO.BarberResponse>> getAllBarbers() {
        List<Barber> barbers = barberRepository.findByIsActive(true);
        return ApiResponse.success("Barbers fetched", barbers.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<List<BarberDTO.BarberResponse>> getAvailableBarbers() {
        List<Barber> barbers = barberRepository.findByStatusAndIsActive(Barber.BarberStatus.AVAILABLE, true);
        return ApiResponse.success("Available barbers fetched", barbers.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    public ApiResponse<BarberDTO.BarberResponse> createBarber(BarberDTO.CreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already registered");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.BARBER);
        userRepository.save(user);

        Barber barber = new Barber();
        barber.setUser(user);
        barber.setSpecialization(request.getSpecialization());
        barber.setExperienceYears(request.getExperienceYears());
        barber.setStatus(Barber.BarberStatus.AVAILABLE);
        barberRepository.save(barber);

        return ApiResponse.success("Barber created successfully", toResponse(barber));
    }

    public ApiResponse<BarberDTO.BarberResponse> updateBarber(Long id, BarberDTO.CreateRequest request) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found with id: " + id));
        barber.setSpecialization(request.getSpecialization());
        barber.setExperienceYears(request.getExperienceYears());
        if (request.getName() != null) barber.getUser().setName(request.getName());
        if (request.getPhone() != null) barber.getUser().setPhone(request.getPhone());
        userRepository.save(barber.getUser());
        barberRepository.save(barber);
        return ApiResponse.success("Barber updated", toResponse(barber));
    }

    public ApiResponse<Void> deleteBarber(Long id) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found with id: " + id));
        barber.setIsActive(false);
        barberRepository.save(barber);
        return ApiResponse.success("Barber deleted", null);
    }

    public ApiResponse<BarberDTO.BarberResponse> updateStatus(Long id, String status) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barber not found with id: " + id));
        barber.setStatus(Barber.BarberStatus.valueOf(status));
        barberRepository.save(barber);
        return ApiResponse.success("Status updated", toResponse(barber));
    }

    private BarberDTO.BarberResponse toResponse(Barber barber) {
        return new BarberDTO.BarberResponse(
                barber.getId(),
                barber.getUser().getId(),
                barber.getUser().getName(),
                barber.getUser().getEmail(),
                barber.getUser().getPhone(),
                barber.getSpecialization(),
                barber.getExperienceYears(),
                barber.getStatus().name(),
                barber.getIsActive()
        );
    }
}
