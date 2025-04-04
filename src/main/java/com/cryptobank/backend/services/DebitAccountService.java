package com.cryptobank.backend.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.cryptobank.backend.repository.DebitAccountRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebitAccountService {
    
    private final DebitAccountRepository debitAccountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateBalance(String userId, BigDecimal newBalance) {
        debitAccountRepository.increaseBalanceByUserId(userId, newBalance);
        entityManager.flush();
        entityManager.clear();
    }
}
