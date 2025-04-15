package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.repository.DebitWalletDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WithdrawService {

	@Autowired
	DebitWalletDAO debitAccountDAO;

	public Boolean WithdrawIntoSavingAccount(DebitWallet wallet, Double amount){
		if(checkValidBalance(wallet, amount)){
			wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
			debitAccountDAO.save(wallet);
			return true;
		}
		return false;
	}

	public Boolean checkValidBalance(DebitWallet wallet,Double amount){
		BigDecimal currentBalance=wallet.getBalance();
        return currentBalance.compareTo(BigDecimal.valueOf(amount)) > 0;
	}
	


}
