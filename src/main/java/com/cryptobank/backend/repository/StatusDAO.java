package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusDAO extends JpaRepository<Status, String> {

    Status findByName(String name);

}
