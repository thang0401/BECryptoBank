package com.cryptobank.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp,String> {
	
	Optional<UserOtp> findByUser(User user);
	 
	@Query("SELECT u FROM UserOtp u WHERE u.user.id = :userId")
	Optional<UserOtp> getByUserId(String userId);
	
	@Query("SELECT u FROM UserOtp u WHERE u.user.id = :userId")
	UserOtp findByUserId(String userId);
}
