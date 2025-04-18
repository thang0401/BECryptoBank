package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class UserAuthResponse {
	private String id;
    private String role;
    private String email;
    private String fullName;
    private String username;
    private String password; 
    private String avatar;
    private boolean kycStatus;
    private String walletAddress;
    private String firstName;
    private boolean rememberMe;
}
