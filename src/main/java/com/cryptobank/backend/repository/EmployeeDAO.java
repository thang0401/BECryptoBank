package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeDAO extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {
}
