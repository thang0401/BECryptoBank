package com.cryptobank.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.DebitAccount;

@Repository
public interface DebitAccountDAO extends JpaRepository<DebitAccount,String>{
}
