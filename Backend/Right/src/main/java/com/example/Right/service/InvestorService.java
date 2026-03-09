package com.example.Right.service;

import com.example.Right.dto.DealDTO;
import com.example.Right.dto.InterestDTO;
import com.example.Right.dto.InvestorProfileDTO;
import com.example.Right.model.InvestorProfile;
import com.example.Right.model.Deal;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.User;
import com.example.Right.model.InterestStatus;
import com.example.Right.model.DealStatus;
import com.example.Right.model.KYCStatus;
import com.example.Right.repository.InvestorRepository;
import com.example.Right.repository.DealRepository;
import com.example.Right.repository.InvestmentInterestRepository;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestorService {

    private final InvestorRepository investorRepository;
    private final DealRepository dealRepository;
    private final InvestmentInterestRepository interestRepository;
    private final UserRepository userRepository;

    public InvestorProfile getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return investorRepository.findByUser(user).orElseGet(() -> {
            InvestorProfile profile = InvestorProfile.builder()
                    .user(user)
                    .fundSize("₹1 Crore")
                    .investmentPreference("General")
                    .location("India")
                    .experienceYears(5)
                    .portfolioSize(0)
                    .kycStatus(KYCStatus.PENDING)
                    .build();
            return investorRepository.save(profile);
        });
    }

    public InvestorProfile updateProfile(InvestorProfileDTO dto) {
        InvestorProfile profile = getProfile();
        profile.setFundSize(dto.getFundSize());
        profile.setInvestmentPreference(dto.getInvestmentPreference());
        return investorRepository.save(profile);
    }

    public List<DealDTO> getOpenDeals() {
        return dealRepository.findByStatus(DealStatus.OPEN).stream()
                .map(this::mapToDealDTO)
                .collect(Collectors.toList());
    }

    public InvestmentInterest expressInterest(Long dealId) {
        InvestorProfile investor = getProfile();
        Deal deal = dealRepository.findById(dealId).orElseThrow();

        InvestmentInterest interest = InvestmentInterest.builder()
                .deal(deal)
                .investor(investor)
                .status(InterestStatus.PENDING)
                .investmentAmount(0.0) // Default for express interest via simple button
                .equityRequested(0.0)
                .message("Expressed interest via dashboard")
                .build();
        return interestRepository.save(interest);
    }

    public List<InterestDTO> getMyInterests() {
        InvestorProfile investor = getProfile();
        return interestRepository.findByInvestor(investor).stream()
                .map(i -> {
                    InterestDTO dto = new InterestDTO();
                    dto.setId(i.getId());
                    dto.setDealId(i.getDeal().getId());
                    dto.setDealTitle(i.getDeal().getTitle());
                    dto.setInvestorName(i.getInvestor().getUser().getName());
                    dto.setInvestmentAmount(i.getInvestmentAmount());
                    dto.setEquityRequested(i.getEquityRequested());
                    dto.setMessage(i.getMessage());
                    dto.setStatus(i.getStatus().name());
                    dto.setCreatedAt(i.getCreatedAt());
                    return dto;
                }).collect(Collectors.toList());
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
