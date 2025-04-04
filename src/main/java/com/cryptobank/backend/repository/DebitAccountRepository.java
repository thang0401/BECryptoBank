package com.cryptobank.backend.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.DebitAccount;

@Repository
public interface DebitAccountRepository extends JpaRepository<DebitAccount, String> {
	Optional<DebitAccount> findByUserDebitId(String userId);
	
	@Modifying
	@Query("UPDATE DebitAccount d SET d.balance = d.balance + :balance WHERE d.userDebit.id = :userId")
	void increaseBalanceByUserId(@Param("userId") String userId, @Param("balance") BigDecimal balance);

	@Modifying
	@Query("UPDATE DebitAccount d SET d.balance = d.balance - :amount WHERE d.userDebit.id = :userId AND d.balance >= :amount")
	void decreaseBalanceByUserId(@Param("userId") String userId, @Param("amount") BigDecimal amount);

}
