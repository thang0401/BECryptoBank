package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.RoleUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleUrlDAO extends JpaRepository<RoleUrl, String>, JpaSpecificationExecutor<RoleUrl> {

    RoleUrl findByRoleIdAndFunctionUrl(String role, String functionUrl);

}
