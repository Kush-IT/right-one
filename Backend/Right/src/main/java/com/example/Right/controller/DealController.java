package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.dto.DealDTO;
import com.example.Right.model.Deal;
import com.example.Right.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Deal>> createDeal(@RequestBody DealDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Deal created", dealService.createDeal(dto)));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DealDTO>>> getAllDeals() {
        return ResponseEntity.ok(ApiResponse.success("All deals fetched", dealService.getAllDeals()));
    }

    @GetMapping("/open")
    public ResponseEntity<ApiResponse<List<DealDTO>>> getOpenDeals() {
        return ResponseEntity.ok(ApiResponse.success("Open deals fetched", dealService.getOpenDeals()));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DealDTO>>> getMyDeals() {
        return ResponseEntity.ok(ApiResponse.success("My deals fetched", dealService.getMyDeals()));
    }

    @PostMapping("/seed")
    public ResponseEntity<ApiResponse<String>> seedDeals() {
        dealService.seedDeals();
        return ResponseEntity.ok(ApiResponse.success("20 deals seeded successfully", null));
    }

    @PostMapping("/normalize")
    public ResponseEntity<ApiResponse<String>> normalizeDeals() {
        dealService.normalizeDealAmounts();
        return ResponseEntity.ok(ApiResponse.success("All deals normalized to multiples of 100", null));
    }
}
