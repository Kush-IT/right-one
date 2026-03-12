package com.example.Right.controller;

import com.example.Right.dto.AdminStatsDTO;
import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.UserDTO;
import com.example.Right.dto.KycDTO;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.KYCStatus;
import com.example.Right.model.KycDetails;
import com.example.Right.service.AdminService;
import com.example.Right.service.InvestmentInterestService;
import com.example.Right.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final KycService kycService;
    private final InvestmentInterestService interestService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("All users fetched", adminService.getAllUsers()));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDTO>> getStats() {
        return ResponseEntity.ok(
                ApiResponse.success("Admin stats fetched", adminService.getStats()));
    }

    @GetMapping("/interests/all")
    public ResponseEntity<ApiResponse<List<InvestmentInterest>>> getAllInterests() {
        return ResponseEntity.ok(ApiResponse.success("All interests fetched", interestService.getAllInterests()));
    }

    @GetMapping("/kyc/all")
    public ResponseEntity<ApiResponse<List<KycDTO>>> getAllKyc() {
        return ResponseEntity.ok(ApiResponse.success("Success", kycService.getAllKycRequestsWithRoles()));
    }

    @PutMapping("/kyc/approve/{kycId}")
    public ResponseEntity<KycDetails> approveKyc(@PathVariable("kycId") Long kycId) {
        return ResponseEntity.ok(kycService.updateStatus(kycId, KYCStatus.APPROVED));
    }

    @PutMapping("/kyc/reject/{kycId}")
    public ResponseEntity<KycDetails> rejectKyc(@PathVariable("kycId") Long kycId) {
        return ResponseEntity.ok(kycService.updateStatus(kycId, KYCStatus.REJECTED));
    }
}
