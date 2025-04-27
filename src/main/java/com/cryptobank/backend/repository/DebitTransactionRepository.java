package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cryptobank.backend.entity.DebitTransaction;

public interface DebitTransactionRepository extends JpaRepository<DebitTransaction, String> {
	
	@Modifying
	@Query("SELECT d FROM DebitTransaction d WHERE d.transactionType =:tranType")
	List<DebitTransaction> getAlltransactionByType(String tranType);
	
}
