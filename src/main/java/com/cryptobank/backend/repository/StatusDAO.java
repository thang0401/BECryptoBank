package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDAO extends JpaRepository<Status, String>, JpaSpecificationExecutor<Status> {

    Status findByName(String name);

}
