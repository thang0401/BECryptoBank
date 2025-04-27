package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.SavingTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingTransactionDAO extends JpaRepository<SavingTransaction,String> {
    
	@Modifying
	@Query("SELECT s FROM SavingTransaction s WHERE s.transactionType=:type")
	List<SavingTransaction> GetAllSavingByType(String type);
}
