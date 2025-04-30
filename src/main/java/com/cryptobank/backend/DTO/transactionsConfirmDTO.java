package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class transactionsConfirmDTO {
	private String transactionId;
	private String userId;
	private String newStatus;
}
