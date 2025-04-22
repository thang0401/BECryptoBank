package com.cryptobank.backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "user_bank_account")
public class UserBankAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "bank_name", nullable = false, length = 255)
	private String bankName;

	@Column(name = "account_number", nullable = false, unique = true, length = 50)
	private String accountNumber;

	@Column(name = "account_holder_name", nullable = false, length = 255)
	private String accountHolderName;

	@Column(name = "bank_code", nullable = false, length = 50)
	private String bankCode;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}