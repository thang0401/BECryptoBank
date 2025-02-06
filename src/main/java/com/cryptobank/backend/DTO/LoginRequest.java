package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class LoginRequest {
	private String email;
	private String password;
	private String deviceId;
	private String deviceName;
	private String os;
	private String browser;
	private String ipAddress;
}
