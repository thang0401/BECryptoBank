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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private PayOS payos;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(@RequestBody Map<String, Object> requestBody) {
        String orderId = (String) requestBody.get("orderId");
        Double amount = Double.valueOf(requestBody.get("amount").toString());
        String description = (String) requestBody.get("description");
        String returnUrl = (String) requestBody.get("returnUrl");
        String cancelUrl = (String) requestBody.get("cancelUrl");
        String userId = (String) requestBody.get("userId"); // Lấy user_id từ requestBody

        // Kiểm tra user_id
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "userId không được để trống"));
        }

        // Kiểm tra user trong DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        // Gọi depositToPayOS với userId
        Map<String, String> response = bankTransferService.depositToPayOS(orderId, amount, description, returnUrl, cancelUrl, userId);
        return ResponseEntity.ok(response);
    }

    // API Webhook nhận phản hồi từ PayOS
    @PostMapping("/webhook/payos")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Raw Payload: " + payload);

            ObjectMapper mapper = new ObjectMapper();
            String payloadJson = mapper.writeValueAsString(payload);
            System.out.println("Payload JSON: " + payloadJson);

            if (!payload.containsKey("code") || !payload.containsKey("desc") ||
                !payload.containsKey("data") || !payload.containsKey("signature")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Payload không đúng định dạng: Thiếu các trường code, desc, data hoặc signature");
            }

            Webhook webhook = mapper.readValue(payloadJson, Webhook.class);

//            try {
//                payos.verifyPaymentWebhookData(webhook);
//            } catch (Exception e) {
//                System.err.println("Lỗi xác minh chữ ký: " + e.getMessage());
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("Chữ ký webhook không hợp lệ: " + e.getMessage());
//            }

            WebhookData data = webhook.getData();
            if (data == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không thể lấy dữ liệu từ webhook: data is null");
            }

            BigDecimal amountVND = BigDecimal.valueOf(data.getAmount());
            String description = data.getDescription();

            String userId = extractUserIdFromDescription(description);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không thể trích xuất user_id từ description: " + description);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

            BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
            if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lấy tỷ giá USDC/VND");
            }

            BigDecimal amountUSDC = amountVND.divide(exchangeRate, 6, RoundingMode.HALF_UP);

            // Dùng orderCode làm transactionId
            String transactionId = String.valueOf(data.getOrderCode());

            // Mặc định status là PAID vì webhook không có trường statusp
            String transactionStatus = "Sucesss";
            Status dbStatus = Optional.ofNullable(statusRepository.findByName(transactionStatus))
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + transactionStatus));

            if (transactionRepository.findById(transactionId).isPresent()) {
                return ResponseEntity.ok("Giao dịch đã được xử lý trước đó: " + transactionStatus);
            }

            paymentService.saveTransaction(
                    transactionId,
                    user.getId(),
                    amountVND,
                    amountUSDC,
                    exchangeRate,
                    "DEPOSIT",
                    transactionStatus
            );

            return ResponseEntity.ok("Webhook xử lý thành công: " + transactionStatus);

        } catch (Exception e) {
            System.err.println("Lỗi Webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi xử lý webhook: " + e.getMessage());
        }
    }


    private String extractUserIdFromDescription(String description) {
        if (description == null) {
            return null;
        }

        // Tìm từ "User:" hoặc "user:" không phân biệt hoa thường, rồi lấy chuỗi sau đó cho đến khi gặp dấu | hoặc hết chuỗi
        Pattern pattern = Pattern.compile("(?i)user:\\s*([^|\\s]+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }


    // API rút tiền - Xử lý yêu cầu rút tiền
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
            @RequestParam String newStatus,
            @RequestParam(required = false) Long bankAccountId) {

        Map<String, String> response = bankTransferService.updateTransactionStatus(transactionId, newStatus, bankAccountId);
        return ResponseEntity.ok(response);
    }

    // API để cập nhật trạng thái và cập nhật số dư
    @PutMapping("/transactions/confirm")
    public ResponseEntity<String> confirmTransaction(
            @RequestParam String transactionId,
            @RequestParam String userId,
            @RequestParam String newStatus) {

        // Tìm giao dịch theo ID
        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        // Kiểm tra xem giao dịch có thuộc về user không
        if (!transaction.getDebitWallet().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch không thuộc về user này!");
        }

        // Tìm Status theo tên
        Status status = Optional.ofNullable(statusRepository.findByName(newStatus))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + newStatus));

        // Cập nhật trạng thái
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        // Nếu trạng thái mới là "SUCCESS", cập nhật số dư
        if ("SUCCESS".equalsIgnoreCase(newStatus)) {
            debitWalletService.updateBalance(userId, transaction.getUsdcAmount());
        }

        return ResponseEntity.ok("Trạng thái giao dịch đã được cập nhật thành: " + newStatus);
    }
}
