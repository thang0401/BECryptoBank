package com.cryptobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserOtp;

public interface UserOtpRepository extends JpaRepository<UserOtp,String> {

}
