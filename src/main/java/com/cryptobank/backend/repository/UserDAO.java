package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.model.CustomerUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query("SELECT c FROM User c " +
            "WHERE LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByName(String name);

    @Query("SELECT new com.cryptobank.backend.model.CustomerUserDetails(c.email, c.password, c.role.id) FROM User c WHERE c.email = :email")
    Optional<CustomerUserDetails> authenticate(String email);

}
