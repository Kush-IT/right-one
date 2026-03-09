package com.example.Right.repository;

import com.example.Right.model.Deal;
import com.example.Right.model.DealStatus;
import com.example.Right.model.StartupProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByStartup(StartupProfile startup);
    List<Deal> findByStatus(DealStatus status);
}
