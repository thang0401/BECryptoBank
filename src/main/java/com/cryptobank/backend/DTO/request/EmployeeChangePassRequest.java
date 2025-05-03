package com.cryptobank.backend.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeChangePassRequest {
	private String employeeId;
	private String newPassword;
	private boolean isChangePass;
}
