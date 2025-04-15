package com.cryptobank.backend.services;

<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

>>>>>>> phong
import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.userBankAccountRepository;
<<<<<<< HEAD
import jakarta.transaction.Transactional;
=======

import jakarta.transaction.Transactional;

>>>>>>> phong
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
=======
>>>>>>> phong

@Service
public class BankTransferService2 {

    @Value("${payos.base-url}")
    private String payosBaseUrl;

    @Value("${payos.client-id}")
    private String payosClientId;

    @Value("${payos.api-key}")
    private String payosApiKey;
    
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private DebitWalletService debitWalletService;
	
//	@Autowired
//	private UserService userService;
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private DebitWalletDAO debitWalletRepository;
	
	@Autowired
	private StatusDAO statusRepository;
	
	@Autowired
	private UserDAO userRepository;
	
	@Autowired 
	private UsdcVndTransactionRepository transactionRepository;
	
	@Autowired
	private userBankAccountRepository userBankAccountRepository;

    private final RestTemplate restTemplate;
    private final UserService userService; // Service để kiểm tra số dư USDC
    
    @Autowired
    private PayOSService payosService;


    public BankTransferService2(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    // 1️ API nạp tiền - Trả về mã QR
    public Map<String, String> depositToPayOS(String orderId, Double amount, String description, String returnUrl, String cancelUrl) {
        String url = payosBaseUrl + "/v2/payment-requests";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderId", orderId);
        requestBody.put("amount", amount);
        requestBody.put("description", description);
        requestBody.put("returnUrl", returnUrl);
        requestBody.put("cancelUrl", cancelUrl);
        requestBody.put("clientId", payosClientId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + payosApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        Map<String, String> responseBody = new HashMap<>();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String checkoutUrl = (String) response.getBody().get("checkoutUrl");
            responseBody.put("checkoutUrl", checkoutUrl);
            responseBody.put("qrCodeUrl", "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=" + checkoutUrl);
        } else {
            responseBody.put("error", "Lỗi khi tạo giao dịch!");
        }
        return responseBody;
    }

    // 2️ API rút tiền - Xử lý yêu cầu rút tiền
    @Transactional
    public Map<String, String> requestWithdraw(String userId, BigDecimal usdcAmount, String bankAccount, String bankCode) {
        Map<String, String> responseBody = new HashMap<>();

        // Kiểm tra số dư USDC của user
        DebitWallet debitWallet = debitWalletRepository.findByUserId(userId)
        		.stream()
        		.findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của user: " + userId));

        if (debitWallet.getBalance().compareTo(usdcAmount) < 0) {
            responseBody.put("error", "Số dư không đủ!");
            return responseBody;
        }

        // Lấy tỷ giá USDC/VND
        BigDecimal exchangeRate = BigDecimal.valueOf(exchangeRateService.getUsdcVndRate());
        if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            responseBody.put("error", "Không thể lấy tỷ giá USDC/VND!");
            return responseBody;
        }

        // Chuyển đổi USDC → VND
        BigDecimal vndAmount = usdcAmount.multiply(exchangeRate);

        // Lấy trạng thái "PENDING"
        Status pendingStatus = Optional.ofNullable(statusRepository.findByName("PENDING"))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái PENDING"));

        // Tạo giao dịch rút tiền (chờ duyệt)
        UsdcVndTransaction transaction = new UsdcVndTransaction();
        transaction.setDebitWallet(debitWallet);
        transaction.setVndAmount(vndAmount);
        transaction.setUsdcAmount(usdcAmount);
        transaction.setExchangeRate(exchangeRate);
        transaction.setType("WITHDRAW");
        transaction.setStatus(pendingStatus);
        transactionRepository.save(transaction);

        responseBody.put("message", "Yêu cầu rút tiền đã được gửi, đang chờ xét duyệt!");
        return responseBody;
    }
    
    
    @Transactional
    public Map<String, String> updateTransactionStatus(String transactionId, String newStatus, Long bankAccountId) {
        Map<String, String> responseBody = new HashMap<>();

        // 🔍 Tìm giao dịch
        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        // 🔍 Lấy trạng thái mới từ DB
        Status status = Optional.ofNullable(statusRepository.findByName(newStatus))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + newStatus));

        // ✅ Cập nhật trạng thái giao dịch
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        // 🔥 Nếu giao dịch được duyệt, thực hiện rút tiền
        if ("SUCCESS".equals(newStatus)) {
            DebitWallet debitWallet = transaction.getDebitWallet();
            BigDecimal usdcAmount = transaction.getUsdcAmount();

            if (debitWallet.getBalance().compareTo(usdcAmount) < 0) {
                responseBody.put("error", "Số dư không đủ!");
                return responseBody;
            }

            // 🔍 Nếu không chọn bankAccountId, lấy tài khoản ngân hàng mới nhất
            UserBankAccount bankAccount = userBankAccountRepository.findById(bankAccountId)
<<<<<<< HEAD
            	    .orElseGet(() -> userBankAccountRepository.findFirstByUserIdOrderByModifiedAtDescCreatedAtDesc(
=======
            	    .orElseGet(() -> userBankAccountRepository.findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(
>>>>>>> phong
            	        debitWallet.getUser().getId()
            	    ).orElseThrow(() -> new RuntimeException("Người dùng chưa có tài khoản ngân hàng nào!")));


            // 💰 Trừ số dư USDC trong ví
            debitWallet.setBalance(debitWallet.getBalance().subtract(usdcAmount));
            debitWalletRepository.save(debitWallet);

            // 💳 Gửi yêu cầu rút tiền đến PayOS
            Map<String, String> payosResponse = payosService.withdraw(
                transaction.getVndAmount(), 
                bankAccount.getAccountNumber(), 
                bankAccount.getBankCode()
            );

            if (payosResponse.containsKey("error")) {
                responseBody.put("error", "Lỗi khi gửi yêu cầu rút tiền: " + payosResponse.get("error"));
                return responseBody;
            }

            responseBody.put("message", "Giao dịch đã được duyệt và tiền đang được chuyển!");
        } else if ("FAILED".equals(newStatus)) {
            responseBody.put("message", "Giao dịch đã bị từ chối!");
        }

        return responseBody;
    }




}
