package com.cryptobank.backend.DTO;

import lombok.Data;

@Data
public class UserAuthResponse {
        private String id;
        private String role;
        private String email;
        private String fullName;
        private String username;
        private String avatar;
        private Boolean kycStatus;
        private String walletAddress;
        private String firstName;
        private boolean rememberMe;
        private boolean isBankAccount;
        private String accessToken;
        private String refreshToken;
    }

