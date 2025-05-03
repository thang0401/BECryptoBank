package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.DebitWallet;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DebitWalletDAO extends JpaRepository<DebitWallet,String>{
//	DebitWallet findById(String id);

	List<DebitWallet> findByUserId(String userId);
	
	@Query("SELECT d FROM DebitWallet d WHERE d.user.id=:userId")
	DebitWallet findByOneUserId(String userId);
    
    //Optional<DebitWallet> findByUserDebitId(String userId);
	
	@Modifying
	@Query("UPDATE DebitWallet d SET d.balance = d.balance + :balance WHERE d.user.id = :userId")
	void increaseBalanceByUserId(@Param("userId") String userId, @Param("balance") BigDecimal balance);

	@Modifying
	@Query("UPDATE DebitWallet d SET d.balance = d.balance - :amount WHERE d.user.id = :userId AND d.balance >= :amount")
	void decreaseBalanceByUserId(@Param("userId") String userId, @Param("amount") BigDecimal amount);
	
	

}
