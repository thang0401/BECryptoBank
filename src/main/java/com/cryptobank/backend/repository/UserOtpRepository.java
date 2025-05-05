package com.cryptobank.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import com.cryptobank.backend.entity.DeviceInfo;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp,String> {

	Optional<UserOtp> findByUser(User user);

	@Query("SELECT u FROM UserOtp u WHERE u.user.id = :userId")
	Optional<UserOtp> getByUserId(String userId);

	@Query("SELECT u FROM UserOtp u WHERE u.user.id = :userId")
	UserOtp findByUserId(String userId);
	
	@Query("SELECT uo FROM UserOtp uo JOIN FETCH uo.user WHERE uo.user IS NOT NULL")
    List<UserOtp> findAllWithUser();




}
