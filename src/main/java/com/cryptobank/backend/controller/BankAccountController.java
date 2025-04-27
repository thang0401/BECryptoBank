package com.cryptobank.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.UsdcDTO;
import com.cryptobank.backend.DTO.UserBankAccountDTO;
import com.cryptobank.backend.DTO.VndDTO;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.userBankAccountRepository;
import com.cryptobank.backend.services.ExchangeRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	
	@Autowired
	private UserDAO userRepository;
	
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
	
	//Chuyển đổi từ vnd sang usdc
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
	
	//Chuyển đổi từ usdc sang vnd
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
	
	@PostMapping("/AddAccount/{userId}")
	public ResponseEntity<?> AddUserBankAccount(@RequestBody UserBankAccountDTO userBankAccount)
	{
		try {
			UserBankAccount userAdd=new UserBankAccount();
			userAdd.setAccountHolderName(userBankAccount.getAccontName());
			userAdd.setAccountNumber(userBankAccount.getAccountNumber());
			userAdd.setBankName(userBankAccount.getBankName());
			userAdd.setCreatedAt(LocalDateTime.now());
			userAdd.setDescribe(userBankAccount.getDescribe());
			userAdd.setUser(userRepository.findById(userBankAccount.getUserId()).get());
			UserBankAccount savedAccount = accountRepository.save(userAdd);
			if (savedAccount != null && savedAccount.getId() != null) {
			    return ResponseEntity.ok("Thêm tài khoản ngân hàng thành công");
			} else {
			    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm tài khoản thất bại");
			}
			
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Lỗi trong quá trình thêm tài khoản ngân hàng"+e.getMessage());
		}
	}
}
