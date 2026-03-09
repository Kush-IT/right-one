package com.example.Right.service;

import com.example.Right.dto.DealDTO;
import com.example.Right.model.Deal;
import com.example.Right.model.DealStatus;
import com.example.Right.model.StartupProfile;
import com.example.Right.model.User;
import com.example.Right.repository.DealRepository;
import com.example.Right.repository.StartupRepository;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final StartupRepository startupRepository;
    private final UserRepository userRepository;

    public Deal createDeal(DealDTO dto) {
        StartupProfile profile = getAuthenticatedStartup();
        Deal deal = Deal.builder()
                .startup(profile)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .fundingRequired(dto.getFundingRequired())
                .equityOffered(dto.getEquityOffered())
                .status(DealStatus.OPEN)
                .build();
        return dealRepository.save(deal);
    }

    public List<DealDTO> getAllDeals() {
        return dealRepository.findAll().stream()
                .map(this::mapToDealDTO)
                .collect(Collectors.toList());
    }

    public List<DealDTO> getOpenDeals() {
        return dealRepository.findByStatus(DealStatus.OPEN).stream()
                .map(this::mapToDealDTO)
                .collect(Collectors.toList());
    }

    public List<DealDTO> getMyDeals() {
        StartupProfile profile = getAuthenticatedStartup();
        return dealRepository.findByStartup(profile).stream()
                .map(this::mapToDealDTO)
                .collect(Collectors.toList());
    }

    public void seedDeals() {
        List<StartupProfile> startups = startupRepository.findAll();
        if (startups.isEmpty()) {
            return;
        }

        String[] startupIdeas = {
                "AI Healthcare Platform", "EV Charging Network", "Smart Farming Technology",
                "FinTech Payment Gateway", "Online Education Platform", "Food Delivery Optimization AI",
                "Renewable Energy Marketplace", "Digital Health Records Platform", "Drone Delivery Logistics",
                "SaaS CRM for Small Businesses", "Blockchain Supply Chain", "E-commerce Personalization Engine",
                "Cybersecurity SaaS Platform", "AI Hiring Platform", "Smart City IoT Platform",
                "Carbon Credit Marketplace", "Logistics Route Optimization AI", "Online Legal Services Platform",
                "AI Language Translation Platform", "Remote Work Productivity Platform"
        };

        String[] descriptions = {
                "Revolutionizing healthcare with AI-driven diagnostics.",
                "Expanding the reach of electric vehicles with a robust charging network.",
                "Empowering farmers with IoT and data-driven insights.",
                "Seamless and secure payment solutions for modern businesses.",
                "Making quality education accessible to everyone, everywhere.",
                "Optimizing food delivery routes for faster and fresher service.",
                "Connecting renewable energy producers directly with consumers.",
                "Secure and unified digital storage for medical histories.",
                "Next-Gen logistics powered by autonomous drone technology.",
                "Streamlined customer management for the backbone of the economy.",
                "Transparency and traceability for global supply chains.",
                "Hyper-personalized shopping experiences driven by AI.",
                "Protecting digital assets with advanced threat detection.",
                "Removing bias and improving efficiency in the hiring process.",
                "Connected infrastructure for sustainable and efficient urban living.",
                "Facilitating the trade of carbon offsets to combat climate change.",
                "Advanced algorithms for efficient logistics and fleet management.",
                "Accessible and affordable legal consultation at your fingertips.",
                "Breaking language barriers with real-time AI translation.",
                "Optimizing collaboration and output for distributed teams."
        };

        String[] fundingAmounts = { "₹25,00,000", "₹50,00,000", "₹75,00,000", "₹1,00,00,000", "₹2,50,00,000" };
        Random random = new Random();

        for (int i = 0; i < startupIdeas.length; i++) {
            StartupProfile startup = startups.get(i % startups.size());

            Deal deal = Deal.builder()
                    .title(startupIdeas[i])
                    .description(descriptions[i])
                    .fundingRequired(fundingAmounts[random.nextInt(fundingAmounts.length)])
                    .equityOffered((5 + random.nextInt(16)) + "%")
                    .startup(startup)
                    .status(DealStatus.OPEN)
                    .build();

            dealRepository.save(deal);
        }
    }

    private StartupProfile getAuthenticatedStartup() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return startupRepository.findByUser(user).orElseThrow();
    }

    private DealDTO mapToDealDTO(Deal deal) {
        DealDTO dto = new DealDTO();
        dto.setId(deal.getId());
        dto.setTitle(deal.getTitle());
        dto.setCompanyName(deal.getStartup().getCompanyName());
        dto.setDescription(deal.getDescription());
        dto.setFundingRequired(deal.getFundingRequired());
        dto.setEquityOffered(deal.getEquityOffered());
        dto.setStatus(deal.getStatus().name());
        dto.setCreatedAt(deal.getCreatedAt());
        return dto;
    }

    public void normalizeDealAmounts() {
        List<Deal> deals = dealRepository.findAll();
        for (Deal deal : deals) {
            String raw = deal.getFundingRequired();
            if (raw == null)
                continue;

            // Extract numeric part
            String numericOnly = raw.replaceAll("[^0-9]", "");
            if (numericOnly.isEmpty())
                continue;

            try {
                long amount = Long.parseLong(numericOnly);
                // If the original string contained "Lakh", we should probably adjust if it was
                // like "10 Lakhs" -> 1000000
                if (raw.toLowerCase().contains("lakh")) {
                    amount = amount * 100000;
                }

                long rounded = (amount / 100) * 100;
                deal.setFundingRequired("₹" + rounded);
                dealRepository.save(deal);
            } catch (NumberFormatException e) {
                // Skip if not parseable
            }
        }
    }
}
