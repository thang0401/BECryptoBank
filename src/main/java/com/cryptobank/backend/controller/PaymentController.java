package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.services.BankTransferService2;
import com.cryptobank.backend.services.DebitWalletService;
import com.cryptobank.backend.services.ExchangeRateService;
import com.cryptobank.backend.services.PaymentService;
import com.cryptobank.backend.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	@Autowired
    private BankTransferService2 bankTransferService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DebitWalletService debitWalletService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private UserDAO userRepository;
	
	@Autowired 
	private UsdcVndTransactionRepository transactionRepository;
	
	@Autowired
	private StatusDAO statusRepository;

	
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
            @RequestParam BigDecimal amount,
            @RequestParam String bankAccount,
            @RequestParam String bankCode) {

        Map<String, String> response = bankTransferService.requestWithdraw(userId, amount, bankAccount, bankCode);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/transactions/update-status")
    public ResponseEntity<Map<String, String>> updateTransactionStatus(
            @RequestParam String transactionId, 
            @RequestParam String newStatus) {

        Map<String, String> response = bankTransferService.updateTransactionStatus(transactionId, newStatus);
        return ResponseEntity.ok(response);
    }

    
    
    // API Webhook nhận phản hồi từ PayOS
    @PostMapping("/webhook/payos")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Map<String, Object> payload) {
        String transactionId = (String) payload.get("transaction_id");
        String status = (String) payload.get("status");
        BigDecimal amountVND = BigDecimal.valueOf(Double.parseDouble(payload.get("amount").toString()));
        String userId = (String) payload.get("user_id");

        // 🔍 Tìm user trong DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        // Lấy tỷ giá USDC/VND hiện tại
        BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
        if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể lấy tỷ giá USDC/VND");
        }

        // Chuyển đổi VND → USDC
        BigDecimal amountUSDC = amountVND.divide(exchangeRate, 6, RoundingMode.HALF_UP);

        // Lấy trạng thái "PENDING"
        Status pendingStatus =Optional.ofNullable( statusRepository.findByName("PENDING"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái PENDING"));

        // 🔹 Lưu giao dịch với trạng thái PENDING
        paymentService.saveTransaction(transactionId, user.getId(), amountVND, amountUSDC, exchangeRate, "DEPOSIT", "PENDING");

        return ResponseEntity.ok("Giao dịch đang chờ xác nhận.");
    }
    

    //api để cập nhật trạng thái và cập nhật số dư
    @PutMapping("/transactions/confirm")
    public ResponseEntity<String> confirmTransaction(
            @RequestParam String transactionId, 
            @RequestParam String userId, 
            @RequestParam String newStatus) {

        // 🔍 Tìm giao dịch theo ID
        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        // 🔍 Kiểm tra xem giao dịch có thuộc về user không
        if (!transaction.getDebitWallet().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch không thuộc về user này!");
        }

        // 🔍 Tìm Status theo tên
        Status status = Optional.ofNullable( statusRepository.findByName(newStatus))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + newStatus));

        // ✅ Cập nhật trạng thái
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        // 🔹 Nếu trạng thái mới là "SUCCESS", cập nhật số dư
        if ("SUCCESS".equalsIgnoreCase(newStatus)) {
            debitWalletService.updateBalance(userId, transaction.getUsdcAmount());
        }

        return ResponseEntity.ok("Trạng thái giao dịch đã được cập nhật thành: " + newStatus);
    }

}
