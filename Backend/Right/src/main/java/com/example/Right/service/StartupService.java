package com.example.Right.service;

import com.example.Right.dto.DealDTO;
import com.example.Right.dto.InterestDTO;
import com.example.Right.dto.StartupProfileDTO;
import com.example.Right.model.Deal;
import com.example.Right.model.DealStatus;
import com.example.Right.model.StartupProfile;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.User;
import com.example.Right.model.InterestStatus;
import com.example.Right.model.KYCStatus;
import com.example.Right.repository.DealRepository;
import com.example.Right.repository.InvestmentInterestRepository;
import com.example.Right.repository.StartupRepository;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StartupService {

    private final StartupRepository startupRepository;
    private final DealRepository dealRepository;
    private final InvestmentInterestRepository interestRepository;
    private final UserRepository userRepository;

    public List<InterestDTO> getPortfolio() {
        StartupProfile profile = getProfile();
        List<Deal> myDeals = dealRepository.findByStartup(profile);
        return interestRepository.findAll().stream()
                .filter(i -> i.getDeal() != null && myDeals.contains(i.getDeal())
                        && i.getStatus() == InterestStatus.ACCEPTED)
                .map(this::mapToInterestDTO)
                .collect(Collectors.toList());
    }

    private InterestDTO mapToInterestDTO(InvestmentInterest i) {
        InterestDTO dto = new InterestDTO();
        dto.setId(i.getId());

        if (i.getDeal() != null) {
            dto.setDealId(i.getDeal().getId());
            dto.setDealTitle(i.getDeal().getTitle());
        } else {
            dto.setDealTitle("Unknown Deal");
        }

        if (i.getInvestor() != null && i.getInvestor().getUser() != null) {
            dto.setInvestorName(i.getInvestor().getUser().getName());
        } else {
            dto.setInvestorName("Unknown Investor");
        }

        dto.setInvestmentAmount(i.getInvestmentAmount() != null ? i.getInvestmentAmount() : 0.0);
        dto.setEquityRequested(i.getEquityRequested() != null ? i.getEquityRequested() : 0.0);
        dto.setMessage(i.getMessage() != null ? i.getMessage() : "");
        dto.setStatus(i.getStatus() != null ? i.getStatus().name() : "PENDING");
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }

    public StartupProfile getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return startupRepository.findByUser(user).orElseGet(() -> {
            StartupProfile profile = StartupProfile.builder()
                    .user(user)
                    .companyName(user.getName())
                    .sector("General")
                    .valuation("₹1 Crore")
                    .businessWebsite("https://example.com")
                    .description("Auto-generated profile for " + user.getName())
                    .location("India")
                    .foundingYear(2024)
                    .kycStatus(KYCStatus.PENDING)
                    .build();
            return startupRepository.save(profile);
        });
    }

    public StartupProfile updateProfile(StartupProfileDTO dto) {
        StartupProfile profile = getProfile();
        profile.setCompanyName(dto.getCompanyName());
        profile.setSector(dto.getSector());
        profile.setValuation(dto.getValuation());
        return startupRepository.save(profile);
    }

    public Deal createDeal(DealDTO dto) {
        StartupProfile profile = getProfile();

        // Singleton logic: Check for existing deal
        List<Deal> existingDeals = dealRepository.findByStartup(profile);
        Deal deal;

        if (!existingDeals.isEmpty()) {
            deal = existingDeals.get(0);
            deal.setTitle(dto.getTitle());
            deal.setDescription(dto.getDescription());
            deal.setFundingRequired(dto.getFundingRequired());
            deal.setEquityOffered(dto.getEquityOffered());
        } else {
            deal = Deal.builder()
                    .startup(profile)
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .fundingRequired(dto.getFundingRequired())
                    .equityOffered(dto.getEquityOffered())
                    .status(DealStatus.OPEN)
                    .build();
        }

        return dealRepository.save(deal);
    }

    public List<DealDTO> getMyDeals() {
        StartupProfile profile = getProfile();
        return dealRepository.findByStartup(profile).stream()
                .map(this::mapToDealDTO)
                .collect(Collectors.toList());
    }

    public List<InterestDTO> getInterestsForDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId).orElseThrow();
        return interestRepository.findByDeal(deal).stream()
                .map(this::mapToInterestDTO)
                .collect(Collectors.toList());
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
}
