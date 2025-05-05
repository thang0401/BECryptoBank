package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.BankBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankBalanceRepository extends JpaRepository<BankBalance, String> {
}