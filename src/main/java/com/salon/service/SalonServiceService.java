package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.exception.ResourceNotFoundException;
import com.salon.model.SalonService;
import com.salon.repository.SalonServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SalonServiceService {

    @Autowired
    private SalonServiceRepository repository;

    public ApiResponse<List<SalonService>> getAllActive() {
        return ApiResponse.success("Services fetched", repository.findByIsActive(true));
    }

    public ApiResponse<List<SalonService>> getAll() {
        return ApiResponse.success("Services fetched", repository.findAll());
    }

    public ApiResponse<SalonService> getById(Long id) {
        SalonService service = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return ApiResponse.success("Service fetched", service);
    }

    public ApiResponse<SalonService> create(SalonService service) {
        service.setIsActive(true);
        return ApiResponse.success("Service created successfully", repository.save(service));
    }

    public ApiResponse<SalonService> update(Long id, SalonService updated) {
        SalonService existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setDurationMinutes(updated.getDurationMinutes());
        return ApiResponse.success("Service updated successfully", repository.save(existing));
    }

    public ApiResponse<Void> delete(Long id) {
        SalonService service = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        service.setIsActive(false);
        repository.save(service);
        return ApiResponse.success("Service deleted", null);
    }
}
