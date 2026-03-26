package com.salon.controller;

import com.salon.dto.ApiResponse;
import com.salon.dto.AppointmentDTO;
import com.salon.security.JwtUtil;
import com.salon.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired private AppointmentService appointmentService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<AppointmentDTO.AppointmentResponse>> book(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AppointmentDTO.BookRequest request) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(appointmentService.bookAppointment(userId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AppointmentDTO.AppointmentResponse>>> getMyAppointments(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(appointmentService.getCustomerAppointments(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AppointmentDTO.AppointmentResponse>>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<AppointmentDTO.AppointmentResponse>>> getTodayAppointments() {
        return ResponseEntity.ok(appointmentService.getTodayAppointments());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentDTO.AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, body.get("status")));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentDTO.AppointmentResponse>> cancel(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, userId));
    }
}
