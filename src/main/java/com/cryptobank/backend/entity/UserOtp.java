package com.cryptobank.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_otp")
public class UserOtp {

	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id; // Khóa chính

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "time_start")
	private LocalDateTime timeStart;

	@Column(name = "otp_code")
	private String otpCode;

	@Column(name = "time_end")
	private LocalDateTime timeEnd;

	@PrePersist
	public void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString(); // Tạo id tự động trước khi lưu
		}
	}

	public void randomId()
	{
		this.id = UUID.randomUUID().toString(); // Tạo id tự động thủ công
	}
}
