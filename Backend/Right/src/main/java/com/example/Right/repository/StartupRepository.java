package com.example.Right.repository;

import com.example.Right.model.StartupProfile;
import com.example.Right.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StartupRepository extends JpaRepository<StartupProfile, Long> {
    Optional<StartupProfile> findByUser(User user);
}
