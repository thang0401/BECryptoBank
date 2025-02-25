// package com.cryptobank.backend.services.generalServices;

// import java.util.List;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.cryptobank.backend.entity.UserPortfolio;
// import com.cryptobank.backend.repository.CustomerPortfolioDAO;


// @Service
// public class UserPortfolioService {
// 	@Autowired
// 	CustomerPortfolioDAO customerPortfolioDAO;

	

// 	// Tìm theo customer_id
// 	public List<UserPortfolio> getUserPortfoliosByCustomerId(String customerId) {
// 	    return customerPortfolioDAO.findByCustomerId(customerId);
// 	}

// 	// Tìm theo vai trò của User
// 	public List<UserPortfolio> getUserPortfoliosByRoleName(String roleName) {
// 	    return customerPortfolioDAO.findByRoleName(roleName);
// 	}

// 	// Tìm theo ranking ID
// 	public List<UserPortfolio> getUserPortfoliosByRankingId(String rankingId) {
// 		return customerPortfolioDAO.findByRankingId(rankingId);
// 	}

// 	// Tìm theo số điện thoại
// 	public List<UserPortfolio> getUserPortfoliosByPhoneNumber(String phoneNum) {
// 		return customerPortfolioDAO.findByPhoneNumber(phoneNum);
// 	}

// 	// Tìm theo tên User (first name hoặc last name)
// 	public List<UserPortfolio> getUserPortfoliosByUserName(String name) {
// 		return customerPortfolioDAO.findByUserName(name);
// 	}

// 	// Tìm theo id_card của Heir
// 	public List<UserPortfolio> getUserPortfoliosByIdCard(String idCard) {
// 		return customerPortfolioDAO.findByIdCard(idCard);
// 	}

// 	public List<UserPortfolio> findAll() {
// 		return customerPortfolioDAO.findAll();
// 	}

// 	public UserPortfolio findById(String id) {
// 		return customerPortfolioDAO.findById(id).orElse(null);
// 	}

// 	public boolean existsById(String id) {
// 		return customerPortfolioDAO.existsById(id);
// 	}

// 	public void save(UserPortfolio userPortfolio) {
// 		customerPortfolioDAO.save(userPortfolio);

// 	}

// 	public void deleteById(String id) {
// 		customerPortfolioDAO.deleteById(id);

// 	}
// }
