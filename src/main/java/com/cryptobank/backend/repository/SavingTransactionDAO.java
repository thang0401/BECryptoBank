package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.SavingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingTransactionDAO extends JpaRepository<SavingTransaction,String> {
    
}
