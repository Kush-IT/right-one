package com.example.Right.service;

import com.example.Right.dto.KycDTO;
import com.example.Right.model.KYCStatus;
import com.example.Right.model.KycDetails;
import com.example.Right.model.Role;
import com.example.Right.repository.KycRepository;
import com.example.Right.repository.StartupRepository;
import com.example.Right.repository.InvestorRepository;
import com.example.Right.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KycService {

    @Autowired
    private KycRepository kycRepository;

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private UserRepository userRepository;

    public KycDetails submitKyc(KycDetails kycDetails) {
        return kycRepository.save(kycDetails);
    }

    public Optional<KycDetails> getKycByUserId(Long userId) {
        return kycRepository.findByUserId(userId);
    }

    public List<KycDetails> getAllKycRequests() {
        return kycRepository.findAll();
    }

    public List<KycDTO> getAllKycRequestsWithRoles() {
        List<KYCStatus> relevantStatuses = List.of(KYCStatus.PENDING, KYCStatus.REJECTED);
        return kycRepository.findAllByStatusIn(relevantStatuses).stream().map(kyc -> {
            Role role = userRepository.findById(kyc.getUserId())
                    .map(com.example.Right.model.User::getRole)
                    .orElse(null);

            return KycDTO.builder()
                    .id(kyc.getId())
                    .userId(kyc.getUserId())
                    .fullName(kyc.getFullName())
                    .email(kyc.getEmail())
                    .userRole(role)
                    .businessName(kyc.getBusinessName())
                    .status(kyc.getStatus())
                    .submittedAt(kyc.getSubmittedAt())
                    .aadharCardPath(kyc.getAadharCardPath())
                    .panCardPath(kyc.getPanCardPath())
                    .bankStatementPath(kyc.getBankStatementPath())
                    .businessCertificatePath(kyc.getBusinessCertificatePath())
                    .build();
        }).collect(Collectors.toList());
    }

    public KycDetails updateStatus(Long kycId, KYCStatus status) {
        KycDetails kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC record with ID " + kycId + " not found"));
        kyc.setStatus(status);
        KycDetails savedKyc = kycRepository.save(kyc);

        // Sync with Profile
        syncProfileStatus(kyc.getUserId(), status);

        return savedKyc;
    }

    private void syncProfileStatus(Long userId, KYCStatus status) {
        userRepository.findById(userId).ifPresent(user -> {
            // Check Startup Profile
            startupRepository.findByUser(user).ifPresent(startup -> {
                startup.setKycStatus(status);
                startupRepository.save(startup);
            });

            // Check Investor Profile
            investorRepository.findByUser(user).ifPresent(investor -> {
                investor.setKycStatus(status);
                investorRepository.save(investor);
            });
        });
    }
}
