package com.cryptobank.backend.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.repository.DebitWalletDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebitWalletService {
    
	@Autowired
    private final DebitWalletDAO debitWalletRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateBalance(String userId, BigDecimal newBalance) {
    	debitWalletRepository.increaseBalanceByUserId(userId, newBalance);
        entityManager.flush();
        entityManager.clear();
    }
}
