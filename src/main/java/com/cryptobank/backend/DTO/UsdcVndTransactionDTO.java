package com.cryptobank.backend.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsdcVndTransactionDTO {
	 private String transactionId;
	    private String userId; // Chỉ lấy userId từ User
	    private String debitWalletId; // Chỉ lấy debitWalletId từ DebitWallet
	    private BigDecimal vndAmount;
	    private BigDecimal usdcAmount;
	    private BigDecimal exchangeRate;
	    private String transactionType;
	    private String status;
}
