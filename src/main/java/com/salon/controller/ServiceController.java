package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.model.SalonService;
import com.salon.service.SalonServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private SalonServiceService salonServiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalonService>>> getActiveServices() {
        return ResponseEntity.ok(salonServiceService.getAllActive());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SalonService>>> getAllServices() {
        return ResponseEntity.ok(salonServiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonService>> getService(@PathVariable Long id) {
        return ResponseEntity.ok(salonServiceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalonService>> createService(@RequestBody SalonService service) {
        return ResponseEntity.ok(salonServiceService.create(service));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalonService>> updateService(@PathVariable Long id, @RequestBody SalonService service) {
        return ResponseEntity.ok(salonServiceService.update(id, service));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        return ResponseEntity.ok(salonServiceService.delete(id));
    }
}
