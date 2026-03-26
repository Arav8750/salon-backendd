package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.AppointmentDTO;
import com.salon.dto.BarberDTO;
import com.salon.service.AppointmentService;
import com.salon.service.BarberService;
import com.salon.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    @Autowired private BarberService barberService;
    @Autowired private AppointmentService appointmentService;
    @Autowired private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BarberDTO.BarberResponse>>> getAllBarbers() {
        return ResponseEntity.ok(barberService.getAllBarbers());
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<BarberDTO.BarberResponse>>> getAvailableBarbers() {
        return ResponseEntity.ok(barberService.getAvailableBarbers());
    }

    // Logged-in barber fetches their own profile (barber id + status)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BarberDTO.BarberResponse>> getMyProfile(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(barberService.getBarberByUserId(userId));
    }

    // Logged-in barber fetches their own appointments
    @GetMapping("/my-appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDTO.AppointmentResponse>>> getMyAppointments(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(appointmentService.getBarberAppointments(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BarberDTO.BarberResponse>> createBarber(@RequestBody BarberDTO.CreateRequest request) {
        return ResponseEntity.ok(barberService.createBarber(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BarberDTO.BarberResponse>> updateBarber(@PathVariable Long id, @RequestBody BarberDTO.CreateRequest request) {
        return ResponseEntity.ok(barberService.updateBarber(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBarber(@PathVariable Long id) {
        return ResponseEntity.ok(barberService.deleteBarber(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BarberDTO.BarberResponse>> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(barberService.updateStatus(id, body.get("status")));
    }
}
