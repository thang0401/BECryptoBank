package com.cryptobank.backend.services.generalServices;

import com.cryptobank.backend.entity.SavingAccount;
import com.cryptobank.backend.repository.SavingAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingAccountService {
	@Autowired
	SavingAccountDAO savingAccountDAO;

	// Tìm theo customer_id
	public List<SavingAccount> getUserPortfoliosByCustomerId(String customerId) {
	    return savingAccountDAO.findByCustomerId(customerId);
	}

	// Tìm theo vai trò của User
//	public List<SavingAccount> getUserPortfoliosByRoleName(String roleName) {
//	    return savingAccountDAO.findByRoleName(roleName);
//	}

	// Tìm theo ranking ID
//	public List<SavingAccount> getUserPortfoliosByRankingId(String rankingId) {
//		return savingAccountDAO.findByRankingId(rankingId);
//	}

	// Tìm theo số điện thoại
	public List<SavingAccount> getUserPortfoliosByPhoneNumber(String phoneNum) {
		return savingAccountDAO.findByPhoneNumber(phoneNum);
	}

	// Tìm theo tên User (first name hoặc last name)
	public List<SavingAccount> getUserPortfoliosByUserName(String name) {
		return savingAccountDAO.findByUserName(name);
	}

	// Tìm theo id_card của Heir
//	public List<SavingAccount> getUserPortfoliosByIdCard(String idCard) {
//		return savingAccountDAO.findByIdCard(idCard);
//	}

	public List<SavingAccount> findAll() {
		return savingAccountDAO.findAll();
	}

	public SavingAccount findById(String id) {
		return savingAccountDAO.findById(id).orElse(null);
	}

	public boolean existsById(String id) {
		return savingAccountDAO.existsById(id);
	}

	public void save(SavingAccount userPortfolio) {
		savingAccountDAO.save(userPortfolio);
	}

	public void deleteById(String id) {
		savingAccountDAO.deleteById(id);
	}

}
