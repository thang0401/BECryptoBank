package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.DepositDTO;
import com.cryptobank.backend.DTO.UsdcVndTransactionDTO;
import com.cryptobank.backend.DTO.transactionsConfirmDTO;
import com.cryptobank.backend.DTO.withdrawDTO;
import com.cryptobank.backend.DTO.withdrawStatusDTO;
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.userBankAccountRepository;
import com.cryptobank.backend.services.BankTransferService2;
import com.cryptobank.backend.services.DebitWalletService;
import com.cryptobank.backend.services.ExchangeRateService;
import com.cryptobank.backend.services.PaymentService;
import com.cryptobank.backend.services.StatusService;
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
import java.util.List;
import java.util.Map;
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
    private StatusService statusService;

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
    public ResponseEntity<?> handlePayOSWebhook(@RequestBody Map<String, Object> payload) {
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

            String userId = description;
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không thể trích xuất user_id từ description: " + description);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

            //BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
            BigDecimal exchangeRate = BigDecimal.valueOf(26150.00);
            if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Không thể lấy tỷ giá USDC/VND");
            }
            
            BigDecimal rate = new BigDecimal("0.0000383");
            BigDecimal amountUSDC = amountVND.multiply(rate);
            System.out.println("tổng số usdc: "+amountUSDC);

            // Dùng orderCode làm transactionId
            String transactionId = String.valueOf(data.getOrderCode());
            String transactionStatus;
            Status dbStatus;
            if(data.getCode().equalsIgnoreCase("00"))
            {
            	// Mặc định status là PAID vì webhook không có trường statusp
                 transactionStatus = statusService.getById("cvvveejme6nnaun2s4a0").getName();
                 dbStatus = statusService.getById("cvvveejme6nnaun2s4a0");
            }
            else
            {
            	// Mặc định status là PAID vì webhook không có trường statusp
                 transactionStatus = statusService.getById("cvvvem3me6nnaun2s4b0").getName();
                 dbStatus = statusService.getById("cvvvem3me6nnaun2s4b0");
            }
            

            if (transactionRepository.findById(transactionId).isPresent()) {
                return ResponseEntity.ok("Giao dịch đã được xử lý trước đó: " + transactionStatus);
            }

            UsdcVndTransaction transave=paymentService.saveTransaction(
                    transactionId,
                    user.getId(),
                    amountVND,
                    amountUSDC,
                    exchangeRate,
                    "DEPOSIT",
                    dbStatus.getId()
            );
            
            ResponseEntity<?> entity;
            if(!dbStatus.getId().equalsIgnoreCase("cvvvem3me6nnaun2s4b0"))
            {
            	entity=confirmTransactionFunction(transave.getId(),user.getId(),"cvvvehbme6nnaun2s4ag");
            }
            else
            {
            	entity=failedTransactionFunction(transave.getId(),user.getId(),"cvvvem3me6nnaun2s4b0");
            }
            
            //return ResponseEntity.ok("Webhook xử lý thành công: " + transactionStatus);
            return entity;

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
    public ResponseEntity<String> updateTransactionStatus(
            @RequestBody withdrawStatusDTO withdrawStatus) {

//        Map<String, String> response = bankTransferService.updateTransactionStatus(withdrawStatus.getTransactionId(), withdrawStatus.getNewStatus(), withdrawStatus.getBankAccountId());
//        return ResponseEntity.ok(response);
    	String transactionId=withdrawStatus.getTransactionId();
    	String newStatus=withdrawStatus.getNewStatus();
    	Long bankAccountId=withdrawStatus.getBankAccountId();
    	try {
		 	// Tìm giao dịch
	        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));
	        
	        DebitWallet debitWallet = transaction.getDebitWallet();
            BigDecimal usdcAmount = transaction.getUsdcAmount();
	        System.out.println("Lấy trạng thái mới từ db");
	        // Lấy trạng thái mới từ DB
	        Status status = statusService.getById(newStatus);

	        // Cập nhật trạng thái giao dịch
	        if(transaction.getStatus().getName().equalsIgnoreCase(status.getName()))
	        {
	        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Đơn yêu cầu trên vốn đã được duyệt");
	        }
	        else if(withdrawStatus.getMaGiaoDichBanking()==null)
	        {
	        	System.out.println(withdrawStatus);
	        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn chưa nhập mã giao dịch ngân hàng");
	        }
	        else if(debitWallet.getBalance().compareTo(usdcAmount) < 0)
	        {
	        	Status statusFailed=statusService.getById("cvvvevrme6nnaun2s4cg");
	        	transaction.setStatus(statusFailed);
	        	transaction.setMaGiaoDichBanking(withdrawStatus.getMaGiaoDichBanking());
	        	transaction.setModifiedBy(withdrawStatus.getModifiedBy());
    	        transactionRepository.save(transaction);
	        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Số dư không đủ!");
	        }
	        else
	        {
	        	transaction.setStatus(status);
    	        transactionRepository.save(transaction);
	        }
	        
	        System.out.println("Thực hiện rút tiền");
	        // Nếu giao dịch được duyệt, thực hiện rút tiền
	        if ("cvvvehbme6nnaun2s4ag".equals(newStatus)) { //Transaction Status

	            System.out.println("thực hiện truy vấn user_bank_account");
	            System.out.println("BankAccountID: "+bankAccountId);
	            
	            // Nếu không chọn bankAccountId, lấy tài khoản ngân hàng mới nhất
	            UserBankAccount bankAccount;
	            if (bankAccountId != null) {
	                bankAccount = userBankAccountRepository.findById(bankAccountId)
	                    .orElseGet(() -> userBankAccountRepository
	                        .findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(debitWallet.getUser().getId())
	                        .orElseThrow(() -> new RuntimeException("Người dùng chưa có tài khoản ngân hàng nào!")));
	            } else {
	                bankAccount = userBankAccountRepository
	                    .findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(debitWallet.getUser().getId())
	                    .orElseThrow(() -> new RuntimeException("Người dùng chưa có tài khoản ngân hàng nào!"));
	            }
	            System.out.println("debitWalletuser: "+debitWallet.getUser().getId());
	            System.out.println("thực hiện truy vấn user");
	            String userId=debitWallet.getUser().getId();
	            BigDecimal usdcOld=debitWalletDAO.findByUserId(userId).getBalance();
	            BigDecimal usdcNew=debitWalletDAO.findByUserId(userId).getBalance().subtract(usdcAmount);
	            System.out.println("USDC Cũ: "+usdcOld);
	            System.out.println("USDC Mới: "+usdcNew);
	            System.out.println("Trừ số dư USDC trong ví");
	            // Trừ số dư USDC trong ví
	            //debitWallet.setBalance(debitWallet.getBalance().subtract(usdcAmount));
	            debitWalletService.decreaseBalance(debitWallet.getUser().getId(), usdcAmount);
	            debitWalletService.UpdateVNDBalance(usdcOld, usdcNew);
//	            debitWalletRepository.save(debitWallet);

	            // Gửi yêu cầu rút tiền đến PayOS
//	            Map<String, String> payosResponse = payosService.withdraw(
//	                transaction.getVndAmount(), 
//	                bankAccount.getAccountNumber(), 
//	                bankAccount.getBankCode()
//	            );
//
//	            if (payosResponse.containsKey("error")) {
//	                responseBody.put("error", "Lỗi khi gửi yêu cầu rút tiền: " + payosResponse.get("error"));
//	                return responseBody;
//	            }
	        } else if ("cvvvem3me6nnaun2s4b0".equalsIgnoreCase(newStatus)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch đã bị từ chối!");
	        }

	        
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lỗi khi tạo giao dịch: " + e.getMessage());
		}
    	return ResponseEntity.ok("Giao dịch đã được duyệt và tiền đang được chuyển!");
    }

    // API Admin xác nhận yêu cầu nạp tiền 
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
        Status status = statusService.getById(Transaction.getNewStatus());

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
       
        BigDecimal usdcOld=debitWalletDAO.findByUserId(Transaction.getUserId()).getBalance();
        BigDecimal usdcNew=debitWalletDAO.findByUserId(Transaction.getUserId()).getBalance().add(transaction.getUsdcAmount());
        // Nếu trạng thái mới là "SUCCESS", cập nhật số dư
        if ("cvvvehbme6nnaun2s4ag".equalsIgnoreCase(Transaction.getNewStatus())) {
        	//debitWalletService.updateUsdcBalance();
            debitWalletService.updateBalance(Transaction.getUserId(), transaction.getUsdcAmount());
            debitWalletService.UpdateVNDBalance(usdcOld, usdcNew);
        }

        return ResponseEntity.ok("Trạng thái giao dịch đã được cập nhật thành: " + status.getName());
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
                        tx.getStatus().getName(),
                        tx.getCreatedAt().toLocalDateTime()
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
                        tx.getStatus().getName(),
                        tx.getCreatedAt().toLocalDateTime()
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
        Status pendingStatus = statusService.getById("cvvveejme6nnaun2s4a0"); //success

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
                        tx.getStatus().getName(),
                        tx.getCreatedAt().toLocalDateTime()
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
    
    public ResponseEntity<?> confirmTransactionFunction(String transactionId,String userId,String statusId)
    {
    	System.out.println("Xử lý nạp tiền confirm");
    	 // Tìm giao dịch theo ID
        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        System.out.println("Kiểm tra xem giao dịch có thuộc về user không");
        // Kiểm tra xem giao dịch có thuộc về user không
        if (!transaction.getDebitWallet().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch không thuộc về user này!");
        }

        System.out.println("Tìm Status theo tên");
        // Tìm Status theo tên
        Status status = statusService.getById(statusId);

        System.out.println("Cập nhật trạng thái");
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
       
        BigDecimal usdcOld=debitWalletDAO.findByUserId(userId).getBalance();
        BigDecimal usdcNew=debitWalletDAO.findByUserId(userId).getBalance().add(transaction.getUsdcAmount());
        System.out.println("Nếu trạng thái mới là \"SUCCESS\", cập nhật số dư");
        // Nếu trạng thái mới là "SUCCESS", cập nhật số dư
        if ("cvvvehbme6nnaun2s4ag".equalsIgnoreCase(statusId)) {
        	//debitWalletService.updateUsdcBalance();
            debitWalletService.updateBalance(userId, transaction.getUsdcAmount());
            debitWalletService.UpdateVNDBalanceDeposit(usdcOld, usdcNew);
        }

        return ResponseEntity.ok("Nạp tiền vào tài khoản thành công");
    }

    public ResponseEntity<?> failedTransactionFunction(String transactionId,String userId,String statusId)
    {
    	UsdcVndTransaction transactionFailed=transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));
    	
    	 System.out.println("Kiểm tra xem giao dịch có thuộc về user không");
         // Kiểm tra xem giao dịch có thuộc về user không
         if (!transactionFailed.getDebitWallet().getUser().getId().equals(userId)) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giao dịch không thuộc về user này!");
         }
    	
    	//Lấy status theo ID
    	Status statusNew=statusService.getById(statusId);
    	transactionFailed.setStatus(statusNew);
    	
    	 System.out.println("Cập nhật trạng thái");
         // Cập nhật trạng thái
    	 transactionFailed.setStatus(statusNew);
    	 transactionRepository.save(transactionFailed);
    	 
    	 return ResponseEntity.badRequest().body("Giao dịch thất bại");
    }
}
