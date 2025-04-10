package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.GoogleAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleAuthRepository extends JpaRepository<GoogleAuth, Long> {
    Optional<GoogleAuth> findByGoogleId(String googleId);
    Optional<GoogleAuth> findByUserId(Long userId);
}