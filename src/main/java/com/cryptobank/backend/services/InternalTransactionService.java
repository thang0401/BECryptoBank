package com.cryptobank.backend.services;

import com.cryptobank.backend.DTO.InternalTransactionRequest;
import com.cryptobank.backend.DTO.InternalTransactionResponse;
import com.cryptobank.backend.entity.BankBalance;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.InternalTransaction;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.TransactionFee;
import com.cryptobank.backend.repository.BankBalanceRepository;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.InternalTransactionRepository;
import com.cryptobank.backend.repository.StatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class InternalTransactionService {

    @Autowired
    private InternalTransactionRepository internalTransactionRepository;

    @Autowired
    private DebitWalletDAO debitWalletDAO;

    @Autowired
    private BankBalanceRepository bankBalanceRepository;

    @Autowired
    private StatusDAO statusDAO;

    @Transactional
    public InternalTransactionResponse transfer(String userId, InternalTransactionRequest request) {
        // Kiểm tra tài khoản gửi và nhận
        DebitWallet fromAccount = debitWalletDAO.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Sender account not found: " + request.getFromAccountId()));

        DebitWallet toAccount = debitWalletDAO.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found: " + request.getToAccountId()));

        // Kiểm tra tài khoản gửi có thuộc về userId không
        if (!fromAccount.getUser().getId().equals(userId)) {
            throw new RuntimeException("Sender account does not belong to userId: " + userId);
        }

        // Kiểm tra số dư tài khoản gửi
        BigDecimal amount = new BigDecimal(request.getAmount());
        BigDecimal feeAmount = new BigDecimal("0.25");
        BigDecimal totalDeduction = amount.add(feeAmount);

        if (fromAccount.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Insufficient balance in sender account");
        }

        // Lấy trạng thái "Pending"
        Status pendingStatus = statusDAO.findById("cvvveejme6nnaun2s4a0")
                .orElseThrow(() -> new RuntimeException("Status 'Pending' not found"));

        // Tạo bản ghi TRANSFER
        InternalTransaction transferTx = new InternalTransaction();
        transferTx.setId(UUID.randomUUID().toString());
        transferTx.setFromAccount(fromAccount);
        transferTx.setToAccount(toAccount);
        transferTx.setAmount(amount);
        transferTx.setType("TRANSFER");
        transferTx.setNote(request.getNote());
        transferTx.setStatus(pendingStatus);
        transferTx.setCreatedAt(OffsetDateTime.now());
        transferTx.setCreatedBy(userId);
        transferTx.setDeleted(false);

        // Tạo bản ghi TransactionFee và thêm vào danh sách transactionFees
        TransactionFee transactionFee = new TransactionFee();
        transactionFee.setId(UUID.randomUUID().toString());
        transactionFee.setInternalTransaction(transferTx);
        transactionFee.setCreatedAt(OffsetDateTime.now());
        transactionFee.setCreatedBy(userId);
        transactionFee.setDeleted(false);
        transferTx.getTransactionFees().add(transactionFee);

        internalTransactionRepository.save(transferTx);

        // Tạo bản ghi RECEIVE
        InternalTransaction receiveTx = new InternalTransaction();
        receiveTx.setId(UUID.randomUUID().toString());
        receiveTx.setFromAccount(fromAccount);
        receiveTx.setToAccount(toAccount);
        receiveTx.setAmount(BigDecimal.ZERO);
        receiveTx.setType("RECEIVE");
        receiveTx.setNote(request.getNote());
        receiveTx.setStatus(pendingStatus);
        receiveTx.setCreatedAt(OffsetDateTime.now());
        receiveTx.setCreatedBy(userId);
        receiveTx.setDeleted(false);
        internalTransactionRepository.save(receiveTx);

        // Cập nhật số dư tài khoản
        debitWalletDAO.decreaseBalanceByUserId(fromAccount.getUser().getId(), totalDeduction);
        debitWalletDAO.increaseBalanceByUserId(toAccount.getUser().getId(), amount);

        // Cộng phí giao dịch vào bank_balance
        BankBalance bankBalance = bankBalanceRepository.findById("d00tcnk5ig8jm25nu66i")
                .orElseThrow(() -> new RuntimeException("Bank balance record not found"));
        bankBalance.setUsdcBalance(bankBalance.getUsdcBalance().add(feeAmount));
        bankBalance.setUpdatedAt(OffsetDateTime.now());

        bankBalanceRepository.save(bankBalance);

        // Cập nhật trạng thái giao dịch thành "Success"
        Status successStatus = statusDAO.findById("cvvvehbme6nnaun2s4ag")
                .orElseThrow(() -> new RuntimeException("Status 'Success' not found"));
        transferTx.setStatus(successStatus);
        receiveTx.setStatus(successStatus);
        internalTransactionRepository.save(transferTx);
        internalTransactionRepository.save(receiveTx);

        // Tạo phản hồi
        InternalTransactionResponse response = new InternalTransactionResponse();
        response.setTransactionId(transferTx.getId());
        response.setFromAccountId(fromAccount.getId());
        response.setToAccountId(toAccount.getId());
        response.setAmount(amount.toString());
        response.setFee(feeAmount.toString());
        response.setNote(request.getNote());
        response.setStatus(successStatus.getName());
        response.setCreatedAt(transferTx.getCreatedAt().toLocalDateTime());

        return response;
    }
}