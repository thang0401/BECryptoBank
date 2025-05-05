package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.DebitTransaction;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.DebitTransactionRepository;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.DTO.DebitTransaction.DepositRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class debitAccountService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DebitTransactionRepository debitTransactionRepository;

    @Autowired
    private DebitWalletDAO debitAccountDAO;

    @Autowired
    private StatusDAO statusDAO;

    @Autowired
    private Web3jService web3jService;

    public List<User> searchUser(String phoneNumber, String email) {
        if (phoneNumber != null) {
            return userDAO.findByPhoneNumberContaining(phoneNumber);
        } else if (email != null) {
            return userDAO.findByEmail(email) != null ? List.of(userDAO.findByEmail(email)) : List.of();
        } else {
            throw new IllegalArgumentException("Phone number or email is required");
        }
    }

    public List<DebitTransaction> getRecentTransactions(String userId) {
        return debitTransactionRepository.findTop5ByCreatedByOrderByCreatedAtDesc(userId);
    }

    public BigDecimal getBalanceByUserId(String userId) {
        DebitWallet debitAccount = debitAccountDAO.findByOneUserId(userId);
        if (debitAccount == null) {
            throw new RuntimeException("Debit account not found for userId: " + userId);
        }
        return debitAccount.getBalance();
    }

    @Transactional
    public DebitTransaction deposit(String userId, DepositRequest request) {
        if (request.getDebitAccountId() == null) {
            throw new IllegalArgumentException("Debit account ID must not be null");
        }

        Optional<DebitTransaction> existingTx = debitTransactionRepository.findById(request.getTransactionHash());
        if (existingTx.isPresent()) {
            throw new IllegalArgumentException("Transaction already processed: " + request.getTransactionHash());
        }

        DebitWallet debitAccount = debitAccountDAO.findById(request.getDebitAccountId())
                .orElseThrow(() -> new RuntimeException("Debit account not found for debitAccountId: " + request.getDebitAccountId()));

        if (!debitAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Debit account does not belong to userId: " + userId);
        }

        Status status = statusDAO.findById("cvvveejme6nnaun2s4a0")
                .orElseThrow(() -> new RuntimeException("Status 'Pending' not found"));

        DebitTransaction transaction = new DebitTransaction();
        transaction.setAmount(new BigDecimal(request.getAmount()));
        transaction.setDebitWallet(debitAccount);
        transaction.setStatus(status);
        transaction.setTransactionType("DEPOSIT");
        transaction.setFromPubKey(request.getFromPubKey());
        transaction.setToPubKey(request.getToPubKey());
        transaction.setTransactionHash(request.getTransactionHash());
        transaction.setCreatedBy(userId);
        debitTransactionRepository.save(transaction);

        try {
            TransactionReceipt receipt = web3jService.signDepositTransaction(
                    request.getFromPubKey(),
                    request.getToPubKey(),
                    request.getAmount(),
                    request.getDebitAccountId(),
                    request.getTransactionHash()
            );
            Status successStatus = statusDAO.findById("cvvvehbme6nnaun2s4ag")
                    .orElseThrow(() -> new RuntimeException("Status 'Success' not found"));
            transaction.setStatus(successStatus);
            // Giữ nguyên lưu blockchainTxHash cho deposit
            // transaction.setBlockchainTxHash(receipt.getTransactionHash());
        } catch (Exception e) {
            Status failedStatus = statusDAO.findById("cvvveejme6nnaun2s4a0")
                    .orElseThrow(() -> new RuntimeException("Status 'Failed' not found"));
            transaction.setStatus(failedStatus);
            throw new RuntimeException("Failed to sign transaction: " + e.getMessage(), e);
        }

        debitTransactionRepository.save(transaction);

        debitAccountDAO.increaseBalanceByUserId(userId, new BigDecimal(request.getAmount()));

        return transaction;
    }

    @Transactional
    public DebitTransaction withdraw(String userId, DepositRequest request) {
        if (request.getDebitAccountId() == null) {
            throw new IllegalArgumentException("Debit account ID must not be null");
        }

        Optional<DebitTransaction> existingTx = debitTransactionRepository.findById(request.getTransactionHash());
        if (existingTx.isPresent()) {
            throw new IllegalArgumentException("Transaction already processed: " + request.getTransactionHash());
        }

        DebitWallet debitAccount = debitAccountDAO.findById(request.getDebitAccountId())
                .orElseThrow(() -> new RuntimeException("Debit account not found for debitAccountId: " + request.getDebitAccountId()));

        if (!debitAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Debit account does not belong to userId: " + userId);
        }

        BigDecimal amount = new BigDecimal(request.getAmount());
        if (debitAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in debit account");
        }

        Status status = statusDAO.findById("cvvveejme6nnaun2s4a0")
                .orElseThrow(() -> new RuntimeException("Status 'Pending' not found"));

        DebitTransaction transaction = new DebitTransaction();
        transaction.setAmount(amount);
        transaction.setDebitWallet(debitAccount);
        transaction.setStatus(status);
        transaction.setTransactionType("WITHDRAW");
        transaction.setFromPubKey(request.getFromPubKey());
        transaction.setToPubKey(request.getToPubKey());
        transaction.setTransactionHash(request.getTransactionHash());
        transaction.setCreatedBy(userId);
        debitTransactionRepository.save(transaction);

        try {
            TransactionReceipt receipt = web3jService.signWithdrawTransaction(
                    request.getFromPubKey(),
                    request.getToPubKey(),
                    request.getAmount(),
                    userId,
                    request.getTransactionHash()
            );
            Status successStatus = statusDAO.findById("cvvvehbme6nnaun2s4ag")
                    .orElseThrow(() -> new RuntimeException("Status 'Success' not found"));
            transaction.setStatus(successStatus);
            // Bỏ lưu blockchainTxHash cho withdraw
            // transaction.setBlockchainTxHash(receipt.getTransactionHash());
        } catch (Exception e) {
            Status failedStatus = statusDAO.findById("cvvveejme6nnaun2s4a0")
                    .orElseThrow(() -> new RuntimeException("Status 'Failed' not found"));
            transaction.setStatus(failedStatus);
            throw new RuntimeException("Failed to sign withdraw transaction: " + e.getMessage(), e);
        }

        debitTransactionRepository.save(transaction);

        debitAccountDAO.decreaseBalanceByUserId(userId, amount);

        return transaction;
    }

    public DebitTransaction getDeposit(String id) {
        return debitTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }
}