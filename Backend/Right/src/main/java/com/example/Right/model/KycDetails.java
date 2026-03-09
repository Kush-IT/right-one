package com.example.Right.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KycDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // SECTION 1 — Personal Details
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String phoneNumber;
    private String email;

    // SECTION 2 — Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    // SECTION 3 — Identity Verification
    private String identityType;
    private String identityNumber;
    private String panNumber;

    // SECTION 4 — Banking Details
    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;

    // SECTION 5 — Business Details
    private String businessName;
    private String businessType;
    private String businessRegistrationNumber;
    private String businessWebsite;

    private boolean termsAccepted;

    // Document Paths
    private String aadharCardPath;
    private String panCardPath;
    private String bankStatementPath;
    private String businessCertificatePath;

    @Enumerated(EnumType.STRING)
    private KYCStatus status;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (status == null)
            status = KYCStatus.PENDING;
    }
}
