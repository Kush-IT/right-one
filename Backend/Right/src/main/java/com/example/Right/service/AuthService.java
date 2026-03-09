package com.example.Right.service;

import com.example.Right.dto.AuthResponse;
import com.example.Right.dto.LoginRequest;
import com.example.Right.dto.SignupRequest;
import com.example.Right.model.*;
import com.example.Right.repository.InvestorRepository;
import com.example.Right.repository.StartupRepository;
import com.example.Right.repository.UserRepository;
import com.example.Right.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final StartupRepository startupRepository;
        private final InvestorRepository investorRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;

        @Transactional
        public AuthResponse register(SignupRequest request) {
                var user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.valueOf(request.getRole().toUpperCase()))
                                .build();
                userRepository.save(user);

                if (user.getRole() == Role.STARTUP) {
                        var profile = StartupProfile.builder()
                                        .user(user)
                                        .companyName(user.getName())
                                        .kycStatus(KYCStatus.PENDING)
                                        .build();
                        startupRepository.save(profile);
                } else if (user.getRole() == Role.INVESTOR) {
                        var profile = InvestorProfile.builder()
                                        .user(user)
                                        .kycStatus(KYCStatus.PENDING)
                                        .build();
                        investorRepository.save(profile);
                }

                var userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .roles(user.getRole().name())
                                .build();

                var jwtToken = jwtUtil.generateToken(userDetails);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .id(user.getId())
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                var user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow();

                var userDetails = org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .roles(user.getRole().name())
                                .build();

                var jwtToken = jwtUtil.generateToken(userDetails);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .email(user.getEmail())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .id(user.getId())
                                .build();
        }
}
