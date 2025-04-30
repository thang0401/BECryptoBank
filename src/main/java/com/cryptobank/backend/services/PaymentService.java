package com.cryptobank.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final UsdcVndTransactionRepository transactionRepository;
    private final DebitWalletDAO debitWalletRepository;
    private final StatusDAO statusRepository;

    public PaymentService(UsdcVndTransactionRepository transactionRepository, 
    		DebitWalletDAO debitWalletRepository,
    		StatusDAO statusRepository) {
        this.transactionRepository = transactionRepository;
        this.debitWalletRepository = debitWalletRepository;
        this.statusRepository = statusRepository;
    }

    @Transactional
    public UsdcVndTransaction saveTransaction(String transactionId, String userId, BigDecimal vndAmount, BigDecimal usdcAmount, BigDecimal exchangeRate, String type, String statusid) {
        // Tìm ví của người dùng
        DebitWallet debitWallet = debitWalletRepository.findByUserId(userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của user: " + userId));

        // Tìm trạng thái
        String currentStatus=statusRepository.getById(statusid).getName();
        Status status = statusRepository.findById(statusid)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + currentStatus));


        // Tạo giao dịch mới
        UsdcVndTransaction transaction = new UsdcVndTransaction();
        transaction.setDebitWallet(debitWallet);
        transaction.setVndAmount(vndAmount);
        transaction.setUsdcAmount(usdcAmount);
        transaction.setExchangeRate(exchangeRate);
        transaction.setType(type);
        transaction.setStatus(status);

        // Lưu giao dịch
        return transactionRepository.save(transaction);
    }

}
