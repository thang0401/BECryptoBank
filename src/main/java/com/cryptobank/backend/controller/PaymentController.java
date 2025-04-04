package com.cryptobank.backend.controller;

import com.cryptobank.backend.repository.DebitAccountRepository;
import com.cryptobank.backend.services.BankTransferService2;
import com.cryptobank.backend.services.DebitAccountService;
import com.cryptobank.backend.services.PaymentService;
import com.cryptobank.backend.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	@Autowired
    private BankTransferService2 bankTransferService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DebitAccountService debitAccountService;
	
	@Autowired
	private UserService userService;

    //API nạp tiền - Trả về mã QR để thanh toán
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(
    		// FE chỉ đưa xuống amount 
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
    
 // API Webhook nhận phản hồi từ PayOS
    @PostMapping("/webhook/payos")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Map<String, Object> payload) {
        String transactionId = (String) payload.get("transaction_id");
        String status = (String) payload.get("status");
        Double amount = Double.valueOf(payload.get("amount").toString());
        BigDecimal balancePlus=BigDecimal.valueOf(amount);
        String userId = (String) payload.get("user_id");

        if ("SUCCESS".equals(status)) {
            // Cập nhật số dư của user
            //userService.increaseUserBalance(userId, amount);
        	debitAccountService.updateBalance(userId, balancePlus);
            // Lưu log giao dịch
            paymentService.saveTransaction(transactionId, userId, amount, "DEPOSIT", "SUCCESS");
        } else {
            // Lưu giao dịch thất bại
            paymentService.saveTransaction(transactionId, userId, amount, "DEPOSIT", "FAILED");
        }

        return ResponseEntity.ok("Webhook received");
    }
}
