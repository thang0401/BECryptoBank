package com.cryptobank.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptobank.backend.entity.DebitWallet;



@Repository
public interface DebitWalletDAO extends JpaRepository<DebitWallet,String>{
    DebitWallet findByWalletAddress(String walletAddress);
}
