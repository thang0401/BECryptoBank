package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.DTO.EmployeeDTO;
import com.cryptobank.backend.DTO.request.EmployeeChangePassRequest;
import com.cryptobank.backend.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.EmployeeLogin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/employee/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Employee", description = "Xác thực nhân viên")
public class AuthEmployeeController {

	private final EmployeeService employeeService;
	
	@PostMapping("/login")
	public AuthResponse login(@RequestBody EmployeeLogin request) {
		return employeeService.login(request);
	}

	@PutMapping("/changePassword")
	@ResponseStatus(HttpStatus.OK)
	@Operation(
		summary = "Thay đổi mật khẩu nhân viên"
	)
	public EmployeeDTO changePassword(@RequestBody EmployeeChangePassRequest request) {
		return employeeService.changePass(request);
	}
	
}
