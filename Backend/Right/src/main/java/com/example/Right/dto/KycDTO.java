package com.example.Right.dto;

import com.example.Right.model.KYCStatus;
import com.example.Right.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KycDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private Role userRole; // New column
    private String businessName;
    private KYCStatus status;
    private LocalDateTime submittedAt;

    // Document Paths
    private String aadharCardPath;
    private String panCardPath;
    private String bankStatementPath;
    private String businessCertificatePath;
}
