package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class LoginRequestAuth {
	private String email;
    private String password;
    private boolean rememberMe;
}
