package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Status;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDAO extends JpaRepository<Status, String>, JpaSpecificationExecutor<Status> {

    Optional<Status> findByName(String name);
    

}
