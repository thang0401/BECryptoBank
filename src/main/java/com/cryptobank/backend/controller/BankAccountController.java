package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.userBankAccountRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/BankAccount")
public class BankAccountController {
	
	@Autowired
	private userBankAccountRepository accountRepository;
	
	
	@GetMapping("/All")
	public ResponseEntity<List<UserBankAccount>> GetAllUserBankAccount() 
	{
		List<UserBankAccount> listUserBank=accountRepository.findAll();
		return ResponseEntity.ok(listUserBank);
	}
	
	
	@GetMapping("/{userId}")
	public ResponseEntity<List<UserBankAccount>> getUserBankAccount(@PathVariable String userId) 
	{
		List<UserBankAccount> listUserBank=accountRepository.findByUser_Id(userId);
		return ResponseEntity.ok(listUserBank);
	}
	
	
	
	
}
