package com.example.Right.repository;

import com.example.Right.model.KycDetails;
import com.example.Right.model.KYCStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<KycDetails, Long> {
    Optional<KycDetails> findByUserId(Long userId);

    List<KycDetails> findAllByStatusIn(List<KYCStatus> statuses);
}
