package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.TransactionHistory;
import com.cryptobank.backend.repository.TransactionHistoryDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryDAO historyDAO;

    @GetMapping
    public ResponseEntity<List<TransactionHistory>> getTransactionHistory() {
        return ResponseEntity.ok(historyDAO.findAll());
    }

//    @GetMapping("/{userId}")
//    public ResponseEntity<List<TransactionHistory>> getTransactionHistoryFromUser(@PathVariable String userId) {
//        return ResponseEntity.ok(historyDAO.getAllTransactionHistory(userId));
//    }

}
