package com.cryptobank.backend.DTO.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatus {
	private String title;
    private Long amount;
    private String avatarColor;
}
