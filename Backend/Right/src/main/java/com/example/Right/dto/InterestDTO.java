package com.example.Right.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InterestDTO {
    private Long id;
    private Long dealId;
    private String dealTitle;
    private String companyName;
    private String investorName;
    private Double investmentAmount;
    private Double equityRequested;
    private String message;
    private String status;
    private String fundingRequired;
    private LocalDateTime createdAt;
}
