package com.cryptobank.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	private String fullname;
	private String email;
	private String phoneNumber;
	private String password;
	private String idCardNumber;
}
