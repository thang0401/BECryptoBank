package com.cryptobank.backend.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.repository.DebitWalletDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class DebitWalletService {

	private static final Logger logger = LoggerFactory.getLogger(DebitWalletService.class);
	
	@Autowired
    private final DebitWalletDAO debitWalletRepository;
	
	@Autowired
	private ExchangeRateService exchangeRateService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateBalance(String userId, BigDecimal newBalance) {
    	debitWalletRepository.increaseBalanceByUserId(userId, newBalance);
        entityManager.flush();
        entityManager.clear();
    }
    
    @Transactional
    public void updateUsdcBalance() {
        // Kiểm tra exchangeRate
    	Double exchangeRate=exchangeRateService.getUsdcVndRate();
    	System.out.println("Tỉ giá: "+exchangeRate);
        if (exchangeRate == null || exchangeRate <= 0) {
            throw new IllegalArgumentException("Invalid exchange rate");
        }

     // Đặt tỷ giá vào biến tạm bằng SELECT set_config
        String rateString = exchangeRate.toString();
        if (rateString.isEmpty()) {
            logger.error("Exchange rate string is empty for value: {}", exchangeRate);
            throw new IllegalStateException("Exchange rate cannot be empty");
        }
        entityManager.createNativeQuery("SELECT set_config('custom.usd_to_vnd', ?, TRUE)")
                .setParameter(1, rateString)
                .getSingleResult();
    }
    
    @Transactional
    public void UpdateVNDBalance(BigDecimal oldUsdc,BigDecimal newUsdc)
    {
    	entityManager.createNativeQuery("SELECT update_vndBalance(:oldUsdc, :newUsdc, :exchangeRate)")
        .setParameter("oldUsdc", oldUsdc)
        .setParameter("newUsdc", newUsdc)
        .setParameter("exchangeRate",BigDecimal.valueOf(exchangeRateService.getUsdcVndRate()))
        .getSingleResult(); // hoặc executeUpdate() nếu không trả về gì

    }
}
