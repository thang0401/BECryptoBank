package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.DTO.AdminSavingAccountDTO;
import com.cryptobank.backend.entity.SavingAccount;

@Repository
public interface SavingAccountDAO extends JpaRepository<SavingAccount,String> {
    @Query(value = "SELECT id,user_id,name,heir_status,balance,term_id,created_date FROM saving_account "
    + " ORDER BY ?1, portfolio_id"
	+ " LIMIT ?2 "
	+ " OFFSET ?3"  , nativeQuery = true)
    public List<SavingAccount> getAllSavingAccountAndPagination(String sortBy,int limit,int offset);

    @Query("SELECT new com.cryptobank.backend.DTO.AdminSavingAccountDTO(s.id ,s.user.id,s.user.firstName,s.user.lastName,s.heirStatus,s.balance,s.term.amount_month,s.term.type,s.createdDate,s.createdDate) FROM SavingAccount s")
    public List<AdminSavingAccountDTO> findAllDTO();

    @Query(value = "SELECT id,user_id,name,heir_status,balance,term_id,created_date FROM saving_account WHERE user_id=?1"
    + " ORDER BY ?2, portfolio_id"
	+ " LIMIT ?3 "
	+ " OFFSET ?4" , nativeQuery = true)
    public List<SavingAccount> getUserSavingAccountAndPagination(String userId,String sortBy,int limit,int offset);
}
