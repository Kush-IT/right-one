package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.AuthResponse;
import com.example.Right.dto.LoginRequest;
import com.example.Right.dto.SignupRequest;
import com.example.Right.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.login(request)));
    }
}
