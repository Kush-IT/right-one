package com.example.Right.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "startup_id")
    private StartupProfile startup;

    private String title;
    private String description;
    private String fundingRequired;
    private String equityOffered;

    @Enumerated(EnumType.STRING)
    private DealStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
