package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBankAccountDTO {
	private String accountNumber;
	private String userId;
	private String accontName;
	private String bankName;
	private String describe;
}
