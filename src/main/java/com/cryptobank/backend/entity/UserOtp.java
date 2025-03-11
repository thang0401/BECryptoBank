package com.cryptobank.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_otp")
public class UserOtp {
	
	@Id
	private String userId; // Khóa chính
	
	@MapsId
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(name="time_start")
	private LocalDateTime timeStart;
	
	@Column(name="otp_code")
	private String otpCode;
	
	@Column(name="time_end")
	private LocalDateTime timeEnd;
}
