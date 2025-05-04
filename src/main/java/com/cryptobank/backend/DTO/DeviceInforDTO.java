package com.cryptobank.backend.DTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInforDTO {
	private String deviceId;
	private String deviceName;
	private String browser;
	private String ipAddress;
	private String os;
	private OffsetDateTime lastLoginAt;
	private Boolean inUse;
	private String userId;
	private String fullName;
}
