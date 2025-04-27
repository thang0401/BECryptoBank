package com.cryptobank.backend.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingTransactionDTO {
	private String id;
	private BigDecimal amount;
	private String savingAccountId;
	private String userId;
	private String transactionType;
}
