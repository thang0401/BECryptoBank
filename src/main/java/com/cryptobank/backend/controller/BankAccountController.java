package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.UsdcDTO;
import com.cryptobank.backend.DTO.VndDTO;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.userBankAccountRepository;
import com.cryptobank.backend.services.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/BankAccount")
public class BankAccountController {
	
	@Autowired
	private userBankAccountRepository accountRepository;
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	
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
	
	
	@PostMapping("/ToUSDC")
	public ResponseEntity<?> changeFromVNDToUsdc(@RequestBody VndDTO vndDto)
	{
		 BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
		    if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
		        return ResponseEntity.badRequest().body("Không lấy được tỷ giá chuyển đổi");
		    }

		    BigDecimal vndAmount = vndDto.getVnd(); // `usdc` ở đây là VND đầu vào
		    if (vndAmount.compareTo(BigDecimal.ZERO) <= 0) {
		        return ResponseEntity.badRequest().body("Số tiền phải lớn hơn 0");
		    }

		    BigDecimal usdcAmount = vndAmount.divide(exchangeRate, 6, RoundingMode.HALF_UP);
		    return ResponseEntity.ok(usdcAmount);
		
	}
	
	@PostMapping("/ToVND")
	public ResponseEntity<?> changeFromVNDToUsdc(@RequestBody UsdcDTO usdc)
	{
		// Lấy tỷ giá USDC/VND
				BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
				if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
					
					return ResponseEntity.badRequest().body("không lấy được tỉ giá chuyển đổi");
				}

				// Chuyển đổi USDC → VND
				BigDecimal vndAmount = usdc.getUsdc().multiply(exchangeRate);
				return ResponseEntity.ok(vndAmount);
	}
	
}
