package com.example.Right.repository;

import com.example.Right.model.InvestorProfile;
import com.example.Right.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<InvestorProfile, Long> {
    Optional<InvestorProfile> findByUser(User user);
}
