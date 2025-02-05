package com.example.BE_Crypto_Bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.BE_Crypto_Bank.dao.CustomerPortfolioDAO;
import com.example.BE_Crypto_Bank.entity.User_portfolio;


@Service
public class UserPortfolioService {
	@Autowired
	CustomerPortfolioDAO customerPortfolioDAO;

	

	// Tìm theo customer_id
	public List<User_portfolio> getUserPortfoliosByCustomerId(String customerId) {
		return customerPortfolioDAO.findByCustomerId(customerId);
	}

	// Tìm theo vai trò của User
	public List<User_portfolio> getUserPortfoliosByRoleName(String roleName) {
		return customerPortfolioDAO.findByRoleName(roleName);
	}

	// Tìm theo ranking ID
	public List<User_portfolio> getUserPortfoliosByRankingId(String rankingId) {
		return customerPortfolioDAO.findByRankingId(rankingId);
	}

	// Tìm theo số điện thoại
	public List<User_portfolio> getUserPortfoliosByPhoneNumber(String phoneNum) {
		return customerPortfolioDAO.findByPhoneNumber(phoneNum);
	}

	// Tìm theo tên User (first name hoặc last name)
	public List<User_portfolio> getUserPortfoliosByUserName(String name) {
		return customerPortfolioDAO.findByUserName(name);
	}

	// Tìm theo id_card của Heir
	public List<User_portfolio> getUserPortfoliosByIdCard(String idCard) {
		return customerPortfolioDAO.findByIdCard(idCard);
	}

	public List<User_portfolio> findAll() {
		return customerPortfolioDAO.findAll();
	}

	public User_portfolio findById(String id) {
		return customerPortfolioDAO.findById(id).orElse(null);
	}

	public boolean existsById(String id) {
		return customerPortfolioDAO.existsById(id);
	}

	public void save(User_portfolio userPortfolio) {
		customerPortfolioDAO.save(userPortfolio);

	}

	public void deleteById(String id) {
		customerPortfolioDAO.deleteById(id);

	}
}
