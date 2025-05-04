package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.DebitTransaction.DebitDTO;
import com.cryptobank.backend.DTO.DebitTransaction.DepositRequest;
import com.cryptobank.backend.DTO.DebitTransaction.DepositResponse;
import com.cryptobank.backend.entity.DebitTransaction;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.debitAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debitAccount")
public class DepositingDebitController {

    @Autowired
    private debitAccountService debitAccountService;

    // API tìm kiếm người dùng (Bước 1, 2, 3, 4, 5)
    @GetMapping("/search")
    public ResponseEntity<List<DebitDTO>> searchUser(
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email) {
        List<User> users = debitAccountService.searchUser(phoneNumber, email);
        List<DebitDTO> userDTOs = users.stream().map(user -> {
            DebitDTO dto = new DebitDTO();
            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            // Kiểm tra null cho DebitAccount
            dto.setDebitAccountId(user.getDebitWalletList() != null ? user.getDebitWalletList().getId() : null);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // API lấy 5 giao dịch gần đây (Bước 0)
    @GetMapping("/recent-transactions")
    public ResponseEntity<List<DebitDTO>> getRecentTransactions() {
        // Giả lập userId (sẽ được thay thế khi tích hợp xác thực)
        String userId = "d0250rm199kgpknaiko0";

        List<DebitTransaction> transactions = debitAccountService.getRecentTransactions(userId);
        List<DebitDTO> transactionDTOs = transactions.stream().map(tx -> {
            DebitDTO dto = new DebitDTO(tx);
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    // API xử lý giao dịch nạp USDC (Bước 12)
    @PostMapping("/deposit")
    public ResponseEntity<DebitDTO> deposit(@RequestBody DepositRequest request) {
        // Giả lập userId (sẽ được thay thế khi tích hợp xác thực)
        String userId = "d0250rm199kgpknaiko0";

        DebitTransaction transaction = debitAccountService.deposit(userId, request);
        DebitDTO dto = new DebitDTO(transaction);
        return ResponseEntity.ok(dto);
    }

    // API lấy thông tin giao dịch để in hóa đơn
    @GetMapping("/{id}")
    public ResponseEntity<DepositResponse> getDeposit(@PathVariable String id) {
        DebitTransaction transaction = debitAccountService.getDeposit(id);
        DepositResponse response = new DepositResponse();
        response.setTransaction(new DebitDTO(transaction));
        response.setUser(new DebitDTO(transaction.getDebitWallet().getUser()));
        return ResponseEntity.ok(response);
    }

    // API lấy số dư của debitAccount thông qua userId
    @GetMapping("/balance/{userId}")
    public ResponseEntity<BigDecimal> getBalanceByUserId(@PathVariable String userId) {
        BigDecimal balance = debitAccountService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<DebitDTO> withdraw(@RequestBody DepositRequest request) {
        String userId = "d0250rm199kgpknaiko0";

        DebitTransaction transaction = debitAccountService.withdraw(userId, request);
        DebitDTO dto = new DebitDTO(transaction);
        return ResponseEntity.ok(dto);
    }

}