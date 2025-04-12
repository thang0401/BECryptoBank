package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.UserBankAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userBankAccountRepository extends JpaRepository<UserBankAccount, Long> {
	Optional<UserBankAccount> findFirstByUserIdOrderByModifiedAtDescCreatedAtDesc(String userId);
}
