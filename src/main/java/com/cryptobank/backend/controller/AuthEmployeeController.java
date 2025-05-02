package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.services.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}
