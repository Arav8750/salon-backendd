package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.CustomerDTO;
import com.salon.exception.ResourceNotFoundException;
import com.salon.model.User;
import com.salon.repository.AppointmentRepository;
import com.salon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public ApiResponse<List<CustomerDTO.CustomerResponse>> getAllCustomers() {
        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);
        List<CustomerDTO.CustomerResponse> response = customers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Customers fetched", response);
    }

    public ApiResponse<CustomerDTO.CustomerResponse> getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return ApiResponse.success("Customer fetched", toResponse(user));
    }

    public ApiResponse<Void> toggleCustomerStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        String status = user.getIsActive() ? "activated" : "deactivated";
        return ApiResponse.success("Customer " + status, null);
    }

    private CustomerDTO.CustomerResponse toResponse(User user) {
        long totalAppointments = appointmentRepository.findByCustomerId(user.getId()).size();
        return new CustomerDTO.CustomerResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getIsActive(),
                user.getCreatedAt(),
                totalAppointments
        );
    }
}
