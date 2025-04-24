package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.DepositDTO;
import com.cryptobank.backend.DTO.UsdcVndTransactionDTO;
import com.cryptobank.backend.DTO.transactionsConfirmDTO;
import com.cryptobank.backend.DTO.withdrawDTO;
import com.cryptobank.backend.DTO.withdrawStatusDTO;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.userBankAccountRepository;
import com.cryptobank.backend.services.BankTransferService2;
import com.cryptobank.backend.services.DebitWalletService;
import com.cryptobank.backend.services.ExchangeRateService;
import com.cryptobank.backend.services.PaymentService;
import com.cryptobank.backend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    
    @Autowired
    private userBankAccountRepository userBankAccountRepository;
    
    @Autowired
    private DebitWalletDAO debitWalletDAO;

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(@RequestBody DepositDTO requestBody) {
       

        // Kiểm tra user_id
        if (requestBody.getUserId() == null || requestBody.getUserId().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "userId không được để trống"));
        }

        // Kiểm tra user trong DB
        User user = userRepository.findById(requestBody.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + requestBody.getUserId()));

        // Gọi depositToPayOS với userId
        Map<String, String> response = bankTransferService.depositToPayOS(requestBody.getOrderId(), requestBody.getAmount(), requestBody.getDescription(), requestBody.getReturnUrl(), requestBody.getCancelUrl(), requestBody.getUserId());
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
            String transactionStatus = "Pending";
            Status dbStatus = statusRepository.findByName(transactionStatus)
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
            @RequestBody withdrawDTO withDraw) {

        Map<String, String> response = bankTransferService.requestWithdraw(withDraw.getUserId(), withDraw.getAmount());
        return ResponseEntity.ok(response);
    }

    //API Admin xác nhận yêu cầu rút tiền
    @PostMapping("/transactions/update-status")
    public ResponseEntity<Map<String, String>> updateTransactionStatus(
            @RequestBody withdrawStatusDTO withdrawStatus) {

        Map<String, String> response = bankTransferService.updateTransactionStatus(withdrawStatus.getTransactionId(), withdrawStatus.getNewStatus(), withdrawStatus.getBankAccountId());
        return ResponseEntity.ok(response);
    }

    // API để cập nhật trạng thái và cập nhật số dư nạp tiền 
    @PutMapping("/transactions/confirm")
    public ResponseEntity<String> confirmTransaction(
            @RequestBody transactionsConfirmDTO Transaction) {

        // Tìm giao dịch theo ID
        UsdcVndTransaction transaction = transactionRepository.findById(Transaction.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + Transaction.getTransactionId()));

        // Kiểm tra xem giao dịch có thuộc về user không
        if (!transaction.getDebitWallet().getUser().getId().equals(Transaction.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch không thuộc về user này!");
        }

        // Tìm Status theo tên
        Status status = statusRepository.findByName(Transaction.getNewStatus())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + Transaction.getNewStatus()));

        // Cập nhật trạng thái
        if(!status.getName().equalsIgnoreCase(transaction.getStatus().getName()))
        {
        	 transaction.setStatus(status);
             transactionRepository.save(transaction);
        }
        else
        {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Trạng thái hiện tại đã là Success");
        }
       
        BigDecimal usdcOld=debitWalletDAO.findByUserId(Transaction.getUserId()).getFirst().getBalance();
        BigDecimal usdcNew=debitWalletDAO.findByUserId(Transaction.getUserId()).getFirst().getBalance().add(transaction.getUsdcAmount());
        // Nếu trạng thái mới là "SUCCESS", cập nhật số dư
        if ("Sucesss".equalsIgnoreCase(Transaction.getNewStatus())) {
        	//debitWalletService.updateUsdcBalance();
            debitWalletService.updateBalance(Transaction.getUserId(), transaction.getUsdcAmount());
            debitWalletService.UpdateVNDBalance(usdcOld, usdcNew);
        }

        return ResponseEntity.ok("Trạng thái giao dịch đã được cập nhật thành: " + Transaction.getNewStatus());
    }
    
 // API lấy tất cả giao dịch của một userId
    @GetMapping("/transactions/user/{userId}")
    public ResponseEntity<List<UsdcVndTransactionDTO>> getTransactionsByUserId(@PathVariable String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

        List<UsdcVndTransaction> transactions = transactionRepository.findByDebitWalletUserId(userId);
        List<UsdcVndTransactionDTO> transactionDTOs = transactions.stream()
                .map(tx -> new UsdcVndTransactionDTO(
                        tx.getId(),
                        tx.getDebitWallet().getUser().getId(),
                        tx.getDebitWallet().getId(),
                        tx.getVndAmount(),
                        tx.getUsdcAmount(),
                        tx.getExchangeRate(),
                        tx.getType(),
                        tx.getStatus().getName()
                ))
                .collect(Collectors.toList());

        if (transactionDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transactionDTOs);
        }

        return ResponseEntity.ok(transactionDTOs);
    }

    // API lấy tất cả giao dịch của toàn bộ người dùng
    @GetMapping("/transactions/all")
    public ResponseEntity<List<UsdcVndTransactionDTO>> getAllTransactions() {
        List<UsdcVndTransaction> transactions = transactionRepository.findAll();
        List<UsdcVndTransactionDTO> transactionDTOs = transactions.stream()
                .map(tx -> new UsdcVndTransactionDTO(
                        tx.getId(),
                        tx.getDebitWallet().getUser().getId(),
                        tx.getDebitWallet().getId(),
                        tx.getVndAmount(),
                        tx.getUsdcAmount(),
                        tx.getExchangeRate(),
                        tx.getType(),
                        tx.getStatus().getName()
                ))
                .collect(Collectors.toList());

        if (transactionDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transactionDTOs);
        }

        return ResponseEntity.ok(transactionDTOs);
    }

    // API lấy tất cả giao dịch có trạng thái PENDING
    @GetMapping("/transactions/pending")
    public ResponseEntity<List<UsdcVndTransactionDTO>> getPendingTransactions() {
        Status pendingStatus = Optional.ofNullable(statusRepository.findById("cvvveejme6nnaun2s4a0"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: PENDING")).get();

        List<UsdcVndTransaction> pendingTransactions = transactionRepository.findByStatus(pendingStatus);
        List<UsdcVndTransactionDTO> transactionDTOs = pendingTransactions.stream()
                .map(tx -> new UsdcVndTransactionDTO(
                        tx.getId(),
                        tx.getDebitWallet().getUser().getId(),
                        tx.getDebitWallet().getId(),
                        tx.getVndAmount(),
                        tx.getUsdcAmount(),
                        tx.getExchangeRate(),
                        tx.getType(),
                        tx.getStatus().getName()
                ))
                .collect(Collectors.toList());

        if (transactionDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transactionDTOs);
        }

        return ResponseEntity.ok(transactionDTOs);
    }

//    @GetMapping("/BankAccount/{userId}")
//    public ResponseEntity<List<UserBankAccount>> getAllBankAccountByUser(@PathVariable String userId)
//    {
//    	List<UserBankAccount> listBankAccount=userBankAccountRepository.findByUser_Id(userId);
//    	return ResponseEntity.ok(listBankAccount);
//    }
    
}
