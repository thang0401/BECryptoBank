package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupStatusDAO extends JpaRepository<GroupStatus, String>, JpaSpecificationExecutor<GroupStatus> {
}
