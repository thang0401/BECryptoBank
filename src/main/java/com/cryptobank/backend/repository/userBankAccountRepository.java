package com.cryptobank.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptobank.backend.entity.UserBankAccount;

public interface userBankAccountRepository extends JpaRepository<UserBankAccount, Long> {
	Optional<UserBankAccount> findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(String userId);
}
