package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.DealDTO;
import com.example.Right.dto.InterestDTO;
import com.example.Right.dto.InvestorProfileDTO;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.InvestorProfile;
import com.example.Right.service.InvestorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investor")
@RequiredArgsConstructor
public class InvestorController {

    private final InvestorService investorService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<InvestorProfile>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", investorService.getProfile()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<InvestorProfile>> updateProfile(@RequestBody InvestorProfileDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", investorService.updateProfile(dto)));
    }

    @GetMapping("/deals/open")
    public ResponseEntity<ApiResponse<List<DealDTO>>> getOpenDeals() {
        return ResponseEntity.ok(ApiResponse.success("Open deals fetched", investorService.getOpenDeals()));
    }

    @PostMapping("/deals/{id}/interest")
    public ResponseEntity<ApiResponse<InvestmentInterest>> expressInterest(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success("Interest expressed", investorService.expressInterest(id)));
    }

    @GetMapping("/interests")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getMyInterests() {
        return ResponseEntity.ok(ApiResponse.success("My interests fetched", investorService.getMyInterests()));
    }
}
