package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class OtpRequest {
	private String otp;
    private String gmail;
    private boolean rememberMe;
}
