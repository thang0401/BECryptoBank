package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsdcVndTransactionRepository extends JpaRepository<UsdcVndTransaction, String> {
	Optional<UsdcVndTransaction> findById(String id); // Nếu dùng ID mặc định
	List<UsdcVndTransaction> findByStatus(Status status);
	List<UsdcVndTransaction> findByDebitWalletUserId(String userId);
	
	@Modifying
	@Query("SELECT t FROM UsdcVndTransaction t WHERE type=:type")
	List<UsdcVndTransaction> getAllUsdcVndtransactionByType(String type);
	
}
