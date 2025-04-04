package com.cryptobank.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	private List<Map<String, Object>> transactions = new ArrayList<>();

    public void saveTransaction(String transactionId, String userId, Double amount, String type, String status) {
        transactions.add(Map.of(
            "transactionId", transactionId,
            "userId", userId,
            "amount", amount,
            "type", type,
            "status", status
        ));
    }

    public List<Map<String, Object>> getAllTransactions() {
        return transactions;
    }
}
