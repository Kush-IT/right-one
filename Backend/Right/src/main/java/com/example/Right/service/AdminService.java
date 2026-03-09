package com.example.Right.service;

import com.example.Right.dto.AdminStatsDTO;
import com.example.Right.dto.UserDTO;
import com.example.Right.model.KYCStatus;
import com.example.Right.repository.InvestmentInterestRepository;
import com.example.Right.repository.DealRepository;
import com.example.Right.repository.InvestorRepository;
import com.example.Right.repository.StartupRepository;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StartupRepository startupRepository;
    private final InvestorRepository investorRepository;
    private final DealRepository dealRepository;
    private final InvestmentInterestRepository investmentInterestRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public AdminStatsDTO getStats() {
        return AdminStatsDTO.builder()
                .totalUsers(userRepository.count())
                .totalStartups(startupRepository.count())
                .totalInvestors(investorRepository.count())
                .totalDeals(dealRepository.count())
                .totalInterests(investmentInterestRepository.count())
                .build();
    }

    public void approveStartupKYC(Long id) {
        var profile = startupRepository.findById(id).orElseThrow();
        profile.setKycStatus(KYCStatus.APPROVED);
        startupRepository.save(profile);
    }

    public void approveInvestorKYC(Long id) {
        var profile = investorRepository.findById(id).orElseThrow();
        profile.setKycStatus(KYCStatus.APPROVED);
        investorRepository.save(profile);
    }
}
