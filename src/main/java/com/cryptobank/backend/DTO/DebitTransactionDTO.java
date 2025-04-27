package com.cryptobank.backend.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitTransactionDTO {
	private String id;
	private BigDecimal amount;
	private String statusName;
	private String DebitWalletId;
	private String transactionType;
	private String transactionHash;
	private String fromPubKey;
	private String toPubKey;
	
}
