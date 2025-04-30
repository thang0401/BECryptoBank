package com.cryptobank.backend.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class withdrawDTO {
	private String userId;
	private BigDecimal amount;
	private String bankAccount;
	private String bankCode;
}
