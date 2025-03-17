package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.model.CustomerUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDAO extends JpaRepository<User, String> {

	// Tìm kiếm chỉ lấy user chưa bị xóa (delete_yn = false)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = false")
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.phoneNumber LIKE %:phone% AND u.deleted = false")
    List<User> findByPhoneNumberContaining(String phone);

    @Query("SELECT u FROM User u " +
            "JOIN u.userRoles ur " +
            "JOIN ur.role r " +
            "WHERE r.name = :role AND u.deleted = false")
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.ranking.name LIKE %:rankingName% AND u.deleted = false")
    List<User> findByRanking_NameContaining(String rankingName);

    @Query("SELECT u FROM User u WHERE u.idCardNumber = :idNumber AND u.deleted = false")
    User findByIdCardNumber(String idNumber);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.deleted = false")
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phoneNumber = :phone AND u.deleted = false")
    boolean existsByPhoneNumber(String phone);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.idCardNumber = :idNumber AND u.deleted = false")
    boolean existsByIdCardNumber(String idNumber);

    @Query("SELECT u FROM User u " +
    	       "WHERE (LOWER(CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.middleName, ''), ' ', COALESCE(u.lastName, ''))) LIKE LOWER(CONCAT('%', :name, '%')) " +
    	       "OR LOWER(CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, ''))) LIKE LOWER(CONCAT('%', :name, '%'))) " +
    	       "AND u.deleted = false")
    	List<User> findByName(@Param("name") String name);

    @Query("SELECT new com.cryptobank.backend.model.CustomerUserDetails(u.email, u.password, 'ROLE_USER') FROM User u " +
            "WHERE u.email = :email AND u.deleted = false")
    CustomerUserDetails authenticate(String email);
    
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllNotDeleted(Pageable pageable);

}
