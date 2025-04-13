package com.cryptobank.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_bank_account")
public class UserBankAccount extends BaseEntity {

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

}
