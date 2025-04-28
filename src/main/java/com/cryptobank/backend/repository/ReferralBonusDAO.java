package com.cryptobank.backend.repository;

import com.cryptobank.backend.entity.ReferralBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReferralBonusDAO extends JpaRepository<ReferralBonus, String>,
    JpaSpecificationExecutor<ReferralBonus> {
}
