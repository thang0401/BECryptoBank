package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomWebhook {
	private String code;
    private String desc;
    private CustomWebhookData data;
    private String signature;
}
