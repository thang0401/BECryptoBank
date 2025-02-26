package com.cryptobank.backend.services.generalServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.DebitAccount;
import com.cryptobank.backend.repository.DebitAccountDAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WithdrawService {

	@Autowired
	DebitAccountDAO debitAccountDAO;

	public Boolean WithdrawIntoSavingAccount(DebitAccount account,Double amount){
		if(checkValidBalance(account, amount)){
			account.setBalance(account.getBalance()-amount);
			debitAccountDAO.save(account);
			return true;
		}
		return false;
	}

	public Boolean checkValidBalance(DebitAccount account,Double amount){
		Double currentBalance=account.getBalance();
		if(amount<currentBalance){
			return true;
		}
		return false;
	}
	


}
