package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.model.CustomerUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDAO extends JpaRepository<User, String> {

    User findByEmail(String email);

    User findByPhone(String phone);

    User findByIdNumber(String idNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByIdNumber(String idNumber);

    /**
     * Lấy danh sách User chứa tên liên quan (bỏ qua hoa thường)
     * @param name Tên cần tìm
     * @return Danh sách user
     */
    @Query("SELECT c FROM User c " +
            "WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByName(String name);

    @Query("SELECT new com.cryptobank.backend.model.CustomerUserDetails(u.email, u.password, 'ROLE_USER') FROM User u " +
            "WHERE u.email = :email")
    CustomerUserDetails authenticate(String email);

}
