package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.UsdcVndTransaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsdcVndTransactionRepository extends JpaRepository<UsdcVndTransaction, String> {
	Optional<UsdcVndTransaction> findById(String id); // Nếu dùng ID mặc định
	
}
