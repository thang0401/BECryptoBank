package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class withdrawStatusDTO {
	private String transactionId;
	private String newStatus;
	private Long bankAccountId;
}
