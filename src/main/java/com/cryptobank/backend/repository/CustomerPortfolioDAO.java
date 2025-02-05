package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptobank.backend.entity.UserPortfolio;





public interface CustomerPortfolioDAO extends JpaRepository<UserPortfolio, String> {
    
	// Tìm theo customer_id
	@Query("SELECT up FROM UserPortfolio up WHERE up.userId.id = :customerId")
	List<UserPortfolio> findByCustomerId(@Param("customerId") String customerId);

	// Truy vấn User_portfolio theo vai trò của User
	@Query("SELECT up FROM UserPortfolio up WHERE up.userId.role.name = :roleName")
	List<UserPortfolio> findByRoleName(@Param("roleName") String roleName);

	// Truy vấn User_portfolio theo ranking ID
	@Query("SELECT up FROM UserPortfolio up WHERE up.userId.ranking.id = :rankingId")
	List<UserPortfolio> findByRankingId(@Param("rankingId") String rankingId);

	// Truy vấn User_portfolio theo số điện thoại
	@Query("SELECT up FROM UserPortfolio up WHERE up.userId.phone = :phoneNum")
	List<UserPortfolio> findByPhoneNumber(@Param("phoneNum") String phoneNum);

	// Truy vấn User_portfolio theo tên User (first name hoặc last name)
	@Query("SELECT up FROM UserPortfolio up WHERE up.userId.firstName LIKE %:name% OR up.userId.lastName LIKE %:name%")
	List<UserPortfolio> findByUserName(@Param("name") String name);

	// Truy vấn User_portfolio theo id_card của Heir
	@Query("SELECT up FROM UserPortfolio up " +
	       "JOIN up.userId u " +
	       "JOIN Heir h ON u.id = h.createdByUser.id " +
	       "WHERE h.idCard = :idCard")
	List<UserPortfolio> findByIdCard(@Param("idCard") String idCard);
}

