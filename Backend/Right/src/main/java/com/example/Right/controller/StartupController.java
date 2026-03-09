package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.DealDTO;
import com.example.Right.dto.InterestDTO;
import com.example.Right.dto.StartupProfileDTO;
import com.example.Right.model.Deal;
import com.example.Right.model.StartupProfile;
import com.example.Right.service.StartupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/startup")
@RequiredArgsConstructor
public class StartupController {

    private final StartupService startupService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StartupProfile>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", startupService.getProfile()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StartupProfile>> updateProfile(@RequestBody StartupProfileDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", startupService.updateProfile(dto)));
    }

    @PostMapping("/deals")
    public ResponseEntity<ApiResponse<Deal>> createDeal(@RequestBody DealDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Deal created", startupService.createDeal(dto)));
    }

    @GetMapping("/my-deals")
    public ResponseEntity<ApiResponse<List<DealDTO>>> getMyDeals() {
        return ResponseEntity.ok(ApiResponse.success("My deals fetched", startupService.getMyDeals()));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getPortfolio() {
        return ResponseEntity.ok(ApiResponse.success("Startup portfolio fetched", startupService.getPortfolio()));
    }

    @GetMapping("/deals/{id}/interests")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getInterests(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success("Interests fetched", startupService.getInterestsForDeal(id)));
    }
}
