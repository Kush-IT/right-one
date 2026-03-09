package com.example.Right.controller;

import com.example.Right.dto.ApiResponse;
import com.example.Right.model.KYCStatus;
import com.example.Right.model.KycDetails;
import com.example.Right.service.FileUploadService;
import com.example.Right.service.KycService;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;
    private final FileUploadService fileUploadService;
    private final UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<KycDetails>> submitKyc(
            Principal principal,
            @RequestParam("fullName") String fullName,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("gender") String gender,
            @RequestParam("nationality") String nationality,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("addressLine1") String addressLine1,
            @RequestParam("addressLine2") String addressLine2,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("country") String country,
            @RequestParam("postalCode") String postalCode,
            @RequestParam("identityType") String identityType,
            @RequestParam("identityNumber") String identityNumber,
            @RequestParam("panNumber") String panNumber,
            @RequestParam("bankName") String bankName,
            @RequestParam("bankAccountNumber") String bankAccountNumber,
            @RequestParam("ifscCode") String ifscCode,
            @RequestParam("businessName") String businessName,
            @RequestParam("businessType") String businessType,
            @RequestParam("businessRegistrationNumber") String businessRegistrationNumber,
            @RequestParam("businessWebsite") String businessWebsite,
            @RequestParam("termsAccepted") boolean termsAccepted,
            @RequestParam("aadharCard") MultipartFile aadharCard,
            @RequestParam("panCard") MultipartFile panCard,
            @RequestParam("bankStatement") MultipartFile bankStatement,
            @RequestParam("businessCertificate") MultipartFile businessCertificate) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
        }

        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        KycDetails kyc = new KycDetails();
        kyc.setUserId(user.getId());
        kyc.setFullName(fullName);
        kyc.setDateOfBirth(dateOfBirth);
        kyc.setGender(gender);
        kyc.setNationality(nationality);
        kyc.setPhoneNumber(phoneNumber);
        kyc.setEmail(email);
        kyc.setAddressLine1(addressLine1);
        kyc.setAddressLine2(addressLine2);
        kyc.setCity(city);
        kyc.setState(state);
        kyc.setCountry(country);
        kyc.setPostalCode(postalCode);
        kyc.setIdentityType(identityType);
        kyc.setIdentityNumber(identityNumber);
        kyc.setPanNumber(panNumber);
        kyc.setBankName(bankName);
        kyc.setBankAccountNumber(bankAccountNumber);
        kyc.setIfscCode(ifscCode);
        kyc.setBusinessName(businessName);
        kyc.setBusinessType(businessType);
        kyc.setBusinessRegistrationNumber(businessRegistrationNumber);
        kyc.setBusinessWebsite(businessWebsite);
        kyc.setTermsAccepted(termsAccepted);

        // Upload files
        kyc.setAadharCardPath(fileUploadService.storeFile(aadharCard));
        kyc.setPanCardPath(fileUploadService.storeFile(panCard));
        kyc.setBankStatementPath(fileUploadService.storeFile(bankStatement));
        kyc.setBusinessCertificatePath(fileUploadService.storeFile(businessCertificate));

        KycDetails savedKyc = kycService.submitKyc(kyc);
        return ResponseEntity.ok(ApiResponse.success("KYC submitted successfully", savedKyc));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<KycDetails>> getUserKyc(@PathVariable("userId") Long userId) {
        return kycService.getKycByUserId(userId)
                .map(kyc -> ResponseEntity.ok(ApiResponse.success("KYC found", kyc)))
                .orElse(ResponseEntity.ok(ApiResponse.error("KYC not found")));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<KycDetails>>> getAllKyc() {
        return ResponseEntity.ok(ApiResponse.success("Success", kycService.getAllKycRequests()));
    }

    @PutMapping("/admin/approve/{kycId}")
    public ResponseEntity<ApiResponse<KycDetails>> approveKyc(@PathVariable("kycId") Long kycId) {
        return ResponseEntity
                .ok(ApiResponse.success("KYC approved", kycService.updateStatus(kycId, KYCStatus.APPROVED)));
    }

    @PutMapping("/admin/reject/{kycId}")
    public ResponseEntity<ApiResponse<KycDetails>> rejectKyc(@PathVariable("kycId") Long kycId) {
        return ResponseEntity
                .ok(ApiResponse.success("KYC rejected", kycService.updateStatus(kycId, KYCStatus.REJECTED)));
    }
}
