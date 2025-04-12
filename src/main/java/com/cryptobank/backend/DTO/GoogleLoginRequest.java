package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class GoogleLoginRequest {
	private String idToken;
    private boolean rememberMe;
}
