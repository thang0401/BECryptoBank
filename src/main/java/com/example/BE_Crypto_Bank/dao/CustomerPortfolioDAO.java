package com.example.BE_Crypto_Bank.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.BE_Crypto_Bank.entity.User_portfolio;





public interface CustomerPortfolioDAO extends JpaRepository<User_portfolio, String> {
    
    // Tìm theo customer_id
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.id = :customerId")
    List<User_portfolio> findByCustomerId(@Param("customerId") String customerId);

    // Truy vấn User_portfolio theo vai trò của User
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.role.name = :roleName")
    List<User_portfolio> findByRoleName(@Param("roleName") String roleName);

    // Truy vấn User_portfolio theo ranking ID
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.ranking.id = :rankingId")
    List<User_portfolio> findByRankingId(@Param("rankingId") String rankingId);

    // Truy vấn User_portfolio theo số điện thoại
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.phone_num = :phoneNum")
    List<User_portfolio> findByPhoneNumber(@Param("phoneNum") String phoneNum);

    // Truy vấn User_portfolio theo tên User (first name hoặc last name)
    @Query("SELECT up FROM User_portfolio up WHERE up.user_id.first_name LIKE %:name% OR up.user_id.last_name LIKE %:name%")
    List<User_portfolio> findByUserName(@Param("name") String name);

    // Truy vấn User_portfolio theo id_card của Heir
    @Query("SELECT up FROM User_portfolio up " +
           "JOIN up.user_id u " +
           "JOIN Heir h ON u.id = h.createdByUser.id " +
           "WHERE h.id_card = :idCard")
    List<User_portfolio> findByIdCard(@Param("idCard") String idCard);
}
