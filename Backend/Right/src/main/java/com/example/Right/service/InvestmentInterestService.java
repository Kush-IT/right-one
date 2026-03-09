package com.example.Right.service;

import com.example.Right.dto.InterestDTO;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.InvestorProfile;
import com.example.Right.model.InterestStatus;
import com.example.Right.model.Deal;
import com.example.Right.model.User;
import com.example.Right.model.KYCStatus;
import com.example.Right.repository.InvestmentInterestRepository;
import com.example.Right.repository.DealRepository;
import com.example.Right.repository.InvestorRepository;
import com.example.Right.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentInterestService {

    private final InvestmentInterestRepository interestRepository;
    private final DealRepository dealRepository;
    private final InvestorRepository investorRepository;
    private final UserRepository userRepository;

    public InvestmentInterest expressInterest(InterestDTO dto) {
        InvestorProfile investor = getAuthenticatedInvestor();
        Deal deal = dealRepository.findById(dto.getDealId()).orElseThrow();

        // Round to 3 decimal places (nearest 100 Rupees, since 1 Lakh = 100,000)
        double roundedAmount = Math.round(dto.getInvestmentAmount() * 1000.0) / 1000.0;

        InvestmentInterest interest = InvestmentInterest.builder()
                .deal(deal)
                .investor(investor)
                .investmentAmount(roundedAmount)
                .equityRequested(dto.getEquityRequested())
                .message(dto.getMessage())
                .status(InterestStatus.PENDING)
                .build();
        return interestRepository.save(interest);
    }

    public List<InterestDTO> getInterestsByDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId).orElseThrow();
        return interestRepository.findByDeal(deal).stream()
                .map(this::mapToInterestDTO)
                .collect(Collectors.toList());
    }

    public List<InterestDTO> getPortfolio() {
        InvestorProfile investor = getAuthenticatedInvestor();
        return interestRepository.findByInvestorAndStatus(investor, InterestStatus.ACCEPTED).stream()
                .map(this::mapToInterestDTO)
                .collect(Collectors.toList());
    }

    public List<InterestDTO> getMyInterests() {
        InvestorProfile investor = getAuthenticatedInvestor();
        return interestRepository.findByInvestor(investor).stream()
                .map(this::mapToInterestDTO)
                .collect(Collectors.toList());
    }

    public InvestmentInterest acceptInterest(Long interestId) {
        InvestmentInterest interest = interestRepository.findById(interestId).orElseThrow();
        interest.setStatus(InterestStatus.ACCEPTED);
        return interestRepository.save(interest);
    }

    public InvestmentInterest rejectInterest(Long interestId) {
        InvestmentInterest interest = interestRepository.findById(interestId).orElseThrow();
        interest.setStatus(InterestStatus.REJECTED);
        return interestRepository.save(interest);
    }

    public List<InvestmentInterest> getAllInterests() {
        return interestRepository.findAll();
    }

    private InvestorProfile getAuthenticatedInvestor() {
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

    private InterestDTO mapToInterestDTO(InvestmentInterest i) {
        InterestDTO dto = new InterestDTO();
        dto.setId(i.getId());

        if (i.getDeal() != null) {
            dto.setDealId(i.getDeal().getId());
            dto.setDealTitle(i.getDeal().getTitle());
            dto.setFundingRequired(i.getDeal().getFundingRequired());
            if (i.getDeal().getStartup() != null) {
                dto.setCompanyName(i.getDeal().getStartup().getCompanyName());
            } else {
                dto.setCompanyName("N/A");
            }
        } else {
            dto.setDealTitle("Unknown Deal");
            dto.setCompanyName("N/A");
        }

        if (i.getInvestor() != null && i.getInvestor().getUser() != null) {
            dto.setInvestorName(i.getInvestor().getUser().getName());
        } else {
            dto.setInvestorName("Unknown Investor");
        }

        dto.setInvestmentAmount(i.getInvestmentAmount() != null ? i.getInvestmentAmount() : 0.0);
        dto.setEquityRequested(i.getEquityRequested() != null ? i.getEquityRequested() : 0.0);
        dto.setMessage(i.getMessage());
        dto.setStatus(i.getStatus() != null ? i.getStatus().name() : "PENDING");
        dto.setCreatedAt(i.getCreatedAt());
        return dto;
    }
}
