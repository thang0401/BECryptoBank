package com.cryptobank.backend.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOtpDTO {
	private String otpId;
	private String userId;
	private String userFullName;
	private LocalDateTime timeStart;
	private String otpCode;
	private LocalDateTime timeEnd;
}
