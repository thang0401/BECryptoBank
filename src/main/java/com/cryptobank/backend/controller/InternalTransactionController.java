package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.InternalTransactionRequest;
import com.cryptobank.backend.DTO.InternalTransactionResponse;
import com.cryptobank.backend.services.InternalTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/debitAccount")
public class InternalTransactionController {

    @Autowired
    private InternalTransactionService internalTransactionService;

    @PostMapping("/transfer")
    public ResponseEntity<InternalTransactionResponse> transfer(
            @RequestBody InternalTransactionRequest request) {
        // Giả lập userId (sẽ được thay thế khi tích hợp xác thực)
        String userId = "d0250rm199kgpknaiko0";

        InternalTransactionResponse response = internalTransactionService.transfer(userId, request);
        return ResponseEntity.ok(response);
    }
}