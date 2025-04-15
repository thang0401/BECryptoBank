package com.cryptobank.backend.controllers;

import com.cryptobank.backend.services.generalServices.BankTransferService;
import com.cryptobank.backend.services.generalServices.BankTransferService2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final BankTransferService2 bankTransferService;

    public PaymentController(BankTransferService2 bankTransferService) {
        this.bankTransferService = bankTransferService;
    }

    //API nạp tiền - Trả về mã QR để thanh toán
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(
            @RequestParam String orderId,
            @RequestParam Double amount,
            @RequestParam String description,
            @RequestParam String returnUrl,
            @RequestParam String cancelUrl) {

        Map<String, String> response = bankTransferService.depositToPayOS(orderId, amount, description, returnUrl, cancelUrl);
        return ResponseEntity.ok(response);
    }

    //API rút tiền - Xử lý yêu cầu rút tiền
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(
            @RequestParam String userId,
            @RequestParam Double amount,
            @RequestParam String bankAccount,
            @RequestParam String bankCode) {

        Map<String, String> response = bankTransferService.withdrawFromPayOS(userId, amount, bankAccount, bankCode);
        return ResponseEntity.ok(response);
    }
}
