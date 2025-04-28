package com.cryptobank.backend.DTO;

import java.math.BigDecimal;

import com.cryptobank.backend.entity.DebitWallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDebitAmountDTO {
	private String userId;
	private String debitWalletId;
	private BigDecimal balance;
	private String fullName;
	
	public UserDebitAmountDTO(DebitWallet user) {
	    this.userId = user.getUser().getId();
	    this.debitWalletId = user.getId();
	    this.balance = user.getBalance();
	    this.fullName = user.getUser().getFullName();
	}

}
