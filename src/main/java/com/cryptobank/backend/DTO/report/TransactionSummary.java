package com.cryptobank.backend.DTO.report;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionSummary {
	private String transactionType;
    private Long transactionCount;
    private BigDecimal percentage;
    private String color;
}
