package com.example.Right.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DealDTO {
    private Long id;
    private String title;
    private String companyName;
    private String description;
    private String fundingRequired;
    private String equityOffered;
    private String status;
    private LocalDateTime createdAt;
}
