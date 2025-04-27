package com.cryptobank.backend.DTO.report;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatusSummary {
	private Long totalTransactions;
    private List<TransactionStatus> transactionStatuses;
}
