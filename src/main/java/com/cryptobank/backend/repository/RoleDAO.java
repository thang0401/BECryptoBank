package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    Role findByName(String name);

}
