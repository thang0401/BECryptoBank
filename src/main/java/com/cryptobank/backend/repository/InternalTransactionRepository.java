package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.InternalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalTransactionRepository extends JpaRepository<InternalTransaction, String> {
}