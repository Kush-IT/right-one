package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.InterestDTO;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.service.InvestmentInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InvestmentInterestController {

    private final InvestmentInterestService interestService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InvestmentInterest>> createInterest(@RequestBody InterestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Interest expressed", interestService.expressInterest(dto)));
    }

    @GetMapping("/deal/{dealId}")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getInterestsByDeal(@PathVariable("dealId") Long dealId) {
        return ResponseEntity.ok(ApiResponse.success("Interests fetched", interestService.getInterestsByDeal(dealId)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getMyInterests() {
        return ResponseEntity.ok(ApiResponse.success("My interests fetched", interestService.getMyInterests()));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<ApiResponse<List<InterestDTO>>> getPortfolio() {
        return ResponseEntity.ok(ApiResponse.success("Portfolio fetched", interestService.getPortfolio()));
    }

    @PutMapping("/accept/{interestId}")
    public ResponseEntity<ApiResponse<InvestmentInterest>> acceptInterest(@PathVariable("interestId") Long interestId) {
        return ResponseEntity.ok(ApiResponse.success("Interest accepted", interestService.acceptInterest(interestId)));
    }

    @PutMapping("/reject/{interestId}")
    public ResponseEntity<ApiResponse<InvestmentInterest>> rejectInterest(@PathVariable("interestId") Long interestId) {
        return ResponseEntity.ok(ApiResponse.success("Interest rejected", interestService.rejectInterest(interestId)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<InvestmentInterest>>> getAllInterests() {
        return ResponseEntity.ok(ApiResponse.success("All interests fetched", interestService.getAllInterests()));
    }
}
