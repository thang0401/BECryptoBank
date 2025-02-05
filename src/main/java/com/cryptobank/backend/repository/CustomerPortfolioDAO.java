package com.cryptobank.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cryptobank.backend.entity.UserPortfolio;


public interface CustomerPortfolioDAO extends JpaRepository<UserPortfolio, String> {
    
    // Tìm theo customer_id
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.id = :customerId")
    List<UserPortfolio> findByCustomerId(@Param("customerId") String customerId);

    // Truy vấn User_portfolio theo vai trò của User
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.role.name = :roleName")
    List<UserPortfolio> findByRoleName(@Param("roleName") String roleName);

    // Truy vấn User_portfolio theo ranking ID
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.ranking.id = :rankingId")
    List<UserPortfolio> findByRankingId(@Param("rankingId") String rankingId);

    // Truy vấn User_portfolio theo số điện thoại
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.phone_num = :phoneNum")
    List<UserPortfolio> findByPhoneNumber(@Param("phoneNum") String phoneNum);

    // Truy vấn User_portfolio theo tên User (first name hoặc last name)
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.first_name LIKE %:name% OR up.user_id.last_name LIKE %:name%")
    List<UserPortfolio> findByUserName(@Param("name") String name);

    // Truy vấn User_portfolio theo id_card của Heir
    @Query("SELECT up FROM User_portfolio up " +
           "JOIN up.user_id u " +
           "JOIN Heir h ON u.id = h.createdByUser.id " +
           "WHERE h.id_card = :idCard")
    List<UserPortfolio> findByIdCard(@Param("idCard") String idCard);
}
