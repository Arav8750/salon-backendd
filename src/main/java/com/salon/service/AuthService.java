package com.salon.service;

import com.salon.dto.ApiResponse;
import com.salon.dto.AuthDTO;
import com.salon.model.User;
import com.salon.repository.UserRepository;
import com.salon.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public ApiResponse<AuthDTO.AuthResponse> login(AuthDTO.LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            return ApiResponse.error("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name(), user.getId());

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse(
                token, user.getRole().name(), user.getId(), user.getName(), user.getEmail()
        );

        return ApiResponse.success("Login successful", response);
    }

    public ApiResponse<AuthDTO.AuthResponse> register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.CUSTOMER);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name(), user.getId());

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse(
                token, user.getRole().name(), user.getId(), user.getName(), user.getEmail()
        );

        return ApiResponse.success("Registration successful", response);
    }
}
