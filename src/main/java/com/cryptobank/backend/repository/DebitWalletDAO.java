package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.DebitWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitWalletDAO extends JpaRepository<DebitWallet,String>{

    List<DebitWallet> findByUserId(String userId);

}
