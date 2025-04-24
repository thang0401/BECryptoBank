package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositDTO {
	private String orderId;
	private Double amount;
	private String description;
	private String returnUrl;
	private String cancelUrl;
	private String userId;
}
