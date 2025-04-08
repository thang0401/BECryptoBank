//package com.cryptobank.backend.controller;
//
//import com.cryptobank.backend.services.BankTransferService2;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/withdrawals")
//public class WithdrawalController {
//	 @Autowired
//	 private BankTransferService2 bankTransferService;
//	 
//	 @PostMapping("/transfer")
//	    public ResponseEntity<String> withdraw(@RequestParam String accountNumber,
//	                                           @RequestParam Double amount) {
//	        String result = bankTransferService.transferToBank(accountNumber, amount);
//	        if ("Chuyển tiền thành công!".equals(result)) {
//	            return ResponseEntity.ok(result);
//	        } else {
//	            return ResponseEntity.badRequest().body(result);
//	        }
//	    }
//}
