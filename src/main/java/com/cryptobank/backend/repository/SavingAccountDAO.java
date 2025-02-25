package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavingAccountDAO extends JpaRepository<SavingAccount, String> {
    
	// Tìm theo customer_id
	@Query("SELECT up FROM SavingAccount up WHERE up.user.id = :customerId")
	List<SavingAccount> findByCustomerId(@Param("customerId") String customerId);

	// Truy vấn User_portfolio theo vai trò của User
//	@Query("SELECT up FROM SavingAccount up WHERE up.user.role.name = :roleName")
//	List<SavingAccount> findByRoleName(@Param("roleName") String roleName);

	// Truy vấn User_portfolio theo ranking ID
//	@Query("SELECT up FROM SavingAccount up WHERE up.user.ranking.id = :rankingId")
//	List<SavingAccount> findByRankingId(@Param("rankingId") String rankingId);

	// Truy vấn User_portfolio theo số điện thoại
	@Query("SELECT up FROM SavingAccount up WHERE up.user.phone = :phoneNum")
	List<SavingAccount> findByPhoneNumber(@Param("phoneNum") String phoneNum);

	// Truy vấn User_portfolio theo tên User (first name hoặc last name)
	@Query("SELECT up FROM SavingAccount up WHERE up.user.firstName LIKE %:name% OR up.user.lastName LIKE %:name%")
	List<SavingAccount> findByUserName(@Param("name") String name);

	// Truy vấn User_portfolio theo id_card của Heir
//	@Query("SELECT up FROM SavingAccount up " +
//	       "JOIN up.user u " +
//	       "JOIN Heir h ON u.id = h.createdByUser.id " +
//	       "WHERE h.idCard = :idCard")
//	List<SavingAccount> findByIdCard(@Param("idCard") String idCard);
}

