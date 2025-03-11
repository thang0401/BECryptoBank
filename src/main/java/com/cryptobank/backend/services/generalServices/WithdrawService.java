package com.cryptobank.backend.services.generalServices;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.repository.DebitWalletDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WithdrawService {

	@Autowired
	DebitWalletDAO debitWalletDAO;

	public Boolean TransferIntoSavingAccount(DebitWallet account,BigDecimal amount){
		// getSuccesfulTransaction();
		if(checkValidBalance(account, amount)){
			account.setBalance(account.getBalance().subtract(amount));
			debitWalletDAO.save(account);
			return true;
		}
		return false;
	}

	public Boolean checkValidBalance(DebitWallet account,BigDecimal amount){
		BigDecimal currentBalance=account.getBalance();
		if(currentBalance.compareTo(amount)>=0){
			return true;
		}
		return false;
	}
	


}
