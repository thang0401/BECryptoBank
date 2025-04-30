package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, String>, JpaSpecificationExecutor<UserRole> {
	List<UserRole> findByUserId(String userId);

	@Query("SELECT concat('ROLE ', r.name) FROM UserRole ur " +
		"JOIN ur.role r " +
		"WHERE ur.user.id = :userId")
	List<String> findRoleNameByUserId(String userId);

}
