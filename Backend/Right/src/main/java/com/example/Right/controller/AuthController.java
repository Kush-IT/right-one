package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.AuthResponse;
import com.example.Right.dto.LoginRequest;
import com.example.Right.dto.SignupRequest;
import com.example.Right.dto.VerifyOtpRequest;
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
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email", authService.login(request)));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> resendOtp(@RequestBody com.example.Right.dto.ResendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success("OTP resent to your email", authService.resendOtp(request)));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.verifyOtp(request)));
    }
}
