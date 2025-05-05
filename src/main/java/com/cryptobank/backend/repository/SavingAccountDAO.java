

package com.cryptobank.backend.repository;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavingAccountDAO extends JpaRepository<SavingAccount, String> {
    
	// Tìm theo customer_id
	@Query("SELECT up FROM SavingAccount up WHERE up.user.id = :customerId")
	List<SavingAccount> findByCustomerId(@Param("customerId") String customerId);

	@Query("SELECT up FROM SavingAccount up WHERE upid = :customerId")
	SavingAccount findByIdQuery(@Param("customerId") String customerId);
	// Truy vấn User_portfolio theo vai trò của User
//	@Query("SELECT up FROM SavingAccount up WHERE up.user.role.name = :roleName")
//	List<SavingAccount> findByRoleName(@Param("roleName") String roleName);

	// Truy vấn User_portfolio theo ranking ID
//	@Query("SELECT up FROM SavingAccount up WHERE up.user.ranking.id = :rankingId")
//	List<SavingAccount> findByRankingId(@Param("rankingId") String rankingId);

	// Truy vấn User_portfolio theo số điện thoại
	@Query("SELECT up FROM SavingAccount up WHERE up.user.phoneNumber = :phoneNum")
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
@Query(value = "SELECT id,user_id,name,heir_status,balance,term_id,created_date FROM saving_account "
    + " ORDER BY ?1, portfolio_id"
	+ " LIMIT ?2 "
	+ " OFFSET ?3"  , nativeQuery = true)
    public List<SavingAccount> getAllSavingAccountAndPagination(String sortBy,int limit,int offset);

    @Query("SELECT new com.cryptobank.backend.DTO.AdminSavingAccountDTO(s.id ,s.user.id,s.user.firstName,s.user.lastName,s.heirStatus,s.balance,s.term.amountMonth,s.term.type,s.createdAt,s.createdAt) FROM SavingAccount s")
    public List<AdminSavingAccountDTO> findAllDTO();

    @Query(value = "SELECT id,user_id,name,heir_status,balance,term_id,created_date FROM saving_account WHERE user_id=?1"
    + " ORDER BY ?2, portfolio_id"
	+ " LIMIT ?3 "
	+ " OFFSET ?4" , nativeQuery = true)
    public List<SavingAccount> getUserSavingAccountAndPagination(String userId,String sortBy,int limit,int offset);
}

