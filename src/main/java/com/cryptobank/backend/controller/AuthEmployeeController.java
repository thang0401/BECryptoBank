package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.EmployeeLogin;
import com.cryptobank.backend.entity.Employee;
import com.cryptobank.backend.repository.EmployeeDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/authEmployee")
public class AuthEmployeeController {
	
	@Autowired
	private EmployeeDAO employeeDAO;
	
//	@PostMapping("/login")
//	public ResponseEntity<?> postMethodName(@RequestBody EmployeeLogin employee) {
//		
//		Employee employeeInfor=employeeDAO.findby
//		
//		return entity;
//	}
	
}
