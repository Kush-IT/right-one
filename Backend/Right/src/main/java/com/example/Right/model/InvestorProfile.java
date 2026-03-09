package com.example.Right.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "investor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String fundSize;
    private String investmentPreference;
    private String location;
    private Integer experienceYears;
    private Integer portfolioSize;

    @Enumerated(EnumType.STRING)
    private KYCStatus kycStatus;
}
