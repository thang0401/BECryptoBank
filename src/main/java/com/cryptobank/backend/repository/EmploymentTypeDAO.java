package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmploymentTypeDAO extends JpaRepository<EmploymentType, String>, JpaSpecificationExecutor<EmploymentType> {
}
