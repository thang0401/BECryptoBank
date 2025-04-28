package com.cryptobank.backend.DTO;

import com.cryptobank.backend.entity.DeviceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private DeviceInfo deviceInfo;
    private UserAuthResponse user;
}