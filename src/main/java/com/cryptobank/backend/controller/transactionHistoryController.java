package com.cryptobank.backend.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptobank.backend.DTO.DebitTransactionDTO;
import com.cryptobank.backend.DTO.SavingTransactionDTO;
import com.cryptobank.backend.DTO.UsdcVndTransactionDTO;
import com.cryptobank.backend.DTO.UserDebitAmountDTO;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.repository.DebitTransactionRepository;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.SavingAccountDAO;
import com.cryptobank.backend.repository.SavingTransactionDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;

@RestController
@RequestMapping("/api/TransactionHistory")
public class transactionHistoryController {
	
	@Autowired
	private DebitWalletDAO debitWalletdao;
	
	@Autowired 
	private UsdcVndTransactionRepository repositoryUsdcVndTransactionRepository;
	
	@Autowired
	private DebitTransactionRepository deRepository;
	
	@Autowired
	private SavingTransactionDAO savingTransactionDAO;
	
	@GetMapping("/GetDebitTranByType/{type}")
	public ResponseEntity<?> getAllTranByType(@PathVariable String type)
	{
		try {
			List<?> lisstDebitTran=deRepository.getAlltransactionByType(type)
												.stream()
												.map(debit ->new DebitTransactionDTO(debit.getId(),
																					debit.getAmount(),
																					debit.getStatus().getName(),
																					debit.getDebitWallet().getId(),
																					debit.getTransactionType(),
																					debit.getTransactionHash(),
																					debit.getFromPubKey(),
																					debit.getToPubKey()))
												.collect(Collectors.toList());
					
			return ResponseEntity.ok(lisstDebitTran);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Không thể lấy được các giao dịch Debit Transaction");
		}
	}
	
	@GetMapping("/GetUsdcVndTranByType/{type}")
	public ResponseEntity<?> getUsdcVndTransactionByType(@PathVariable String type)
	{
		try {
			List<?> ListUsdcVndtran=repositoryUsdcVndTransactionRepository.getAllUsdcVndtransactionByType(type)
									.stream()
									.map(transaction ->new UsdcVndTransactionDTO(transaction.getId(),
																				 transaction.getDebitWallet().getUser().getId(),
																				 transaction.getDebitWallet().getId(),
																				 transaction.getVndAmount(),
																				 transaction.getUsdcAmount(),
																				 transaction.getExchangeRate(),
																				 transaction.getType(),
																				 transaction.getStatus().getName()
											)).collect(Collectors.toList());
			
			return ResponseEntity.ok(ListUsdcVndtran);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Lỗi khi lấy lịch sử giao dịch Usdc-Vnd-Transaction");
		}
	}
	
	@GetMapping("/GetSavingTranByType/{type}")
	public ResponseEntity<?> GetSavingTranByType(@PathVariable String type) {
		try {
			List<?> ListSaving=savingTransactionDAO.GetAllSavingByType(type)
												   .stream()
												   .map(saving -> new SavingTransactionDTO(saving.getId(),
														   								   saving.getAmount(),
														   								   saving.getSavingAccount().getId(),
														   								   saving.getUser().getId(),
														   								   saving.getTransactionType()
														   
														   
														   )).collect(Collectors.toList());
			return ResponseEntity.ok(ListSaving);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Không thể lấy được các giao dịch Saving Transaction");
		}
	}
	
	@GetMapping("/GetUserDebitAmount/{userId}")
	public ResponseEntity<?> GetUserDebitAmount(@PathVariable String userId)
	{
		DebitWallet user = debitWalletdao.findByOneUserId(userId);
		UserDebitAmountDTO userDebit = new UserDebitAmountDTO(user);
		return ResponseEntity.ok(userDebit);
	}
	
}
