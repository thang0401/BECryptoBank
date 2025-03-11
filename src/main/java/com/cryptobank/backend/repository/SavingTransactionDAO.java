package com.cryptobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.SavingTransaction;

@Repository
public interface SavingTransactionDAO extends JpaRepository<SavingTransaction,String> {
    
}
