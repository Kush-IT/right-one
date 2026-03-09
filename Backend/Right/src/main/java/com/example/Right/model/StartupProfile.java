package com.example.Right.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "startup_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartupProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String companyName;
    private String sector;
    private String valuation;
    private String businessWebsite;
    private String description;
    private String location;
    private Integer foundingYear;

    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus;
}
