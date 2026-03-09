package com.example.Right.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "investment_interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    private InvestorProfile investor;

    private Double investmentAmount;
    private Double equityRequested;
    private String message;

    @Enumerated(EnumType.STRING)
    private InterestStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
