package com.example.Right.repository;

import com.example.Right.model.Deal;
import com.example.Right.model.InvestmentInterest;
import com.example.Right.model.InvestorProfile;
import com.example.Right.model.InterestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentInterestRepository extends JpaRepository<InvestmentInterest, Long> {
    List<InvestmentInterest> findByDeal(Deal deal);

    List<InvestmentInterest> findByInvestor(InvestorProfile investor);

    List<InvestmentInterest> findByInvestorAndStatus(InvestorProfile investor, InterestStatus status);
}
