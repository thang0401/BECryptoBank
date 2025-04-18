package com.cryptobank.backend.services;

import com.cryptobank.backend.entity.DebitWallet;
import com.cryptobank.backend.entity.Status;
import com.cryptobank.backend.entity.UsdcVndTransaction;
import com.cryptobank.backend.entity.UserBankAccount;
import com.cryptobank.backend.repository.DebitWalletDAO;
import com.cryptobank.backend.repository.StatusDAO;
import com.cryptobank.backend.repository.UsdcVndTransactionRepository;
import com.cryptobank.backend.repository.UserDAO;
import com.cryptobank.backend.repository.userBankAccountRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
    private final UserService userService;
    
    @Autowired
    private PayOSService payosService;

    public BankTransferService2(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    // API nạp tiền - Trả về mã QR
    public Map<String, String> depositToPayOS(String orderId, Double amount, String description, String returnUrl, String cancelUrl) {
        String url = payosBaseUrl.endsWith("/") ? payosBaseUrl + "v2/payment-requests" : payosBaseUrl + "/v2/payment-requests";
        Map<String, String> responseBody = new HashMap<>();

        try {
            // Kiểm tra đầu vào
            if (amount == null || amount <= 0) {
                responseBody.put("error", "amount phải lớn hơn 0");
                return responseBody;
            }
            if (description == null || description.trim().isEmpty()) {
                responseBody.put("error", "description không được để trống");
                return responseBody;
            }
            if (returnUrl == null || returnUrl.trim().isEmpty()) {
                responseBody.put("error", "returnUrl không được để trống");
                return responseBody;
            }
            if (cancelUrl == null || cancelUrl.trim().isEmpty()) {
                responseBody.put("error", "cancelUrl không được để trống");
                return responseBody;
            }

            // Xử lý orderCode
            int orderCode;
            if ("auto".equalsIgnoreCase(orderId)) {
                // Tạo orderCode ngẫu nhiên 6 chữ số (100000-999999)
                orderCode = 100000 + new Random().nextInt(900000);
            } else {
                try {
                    // Thử ép kiểu orderId thành số nguyên
                    orderCode = Integer.parseInt(orderId);
                } catch (NumberFormatException e) {
                    // Nếu không phải số, tạo orderCode ngẫu nhiên 6 chữ số
                    orderCode = 100000 + new Random().nextInt(900000);
                }
            }

            // Tạo body theo yêu cầu của PayOS
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", amount.intValue()); // amount là số nguyên (VND)
            requestBody.put("description", description);
            requestBody.put("returnUrl", returnUrl);
            requestBody.put("cancelUrl", cancelUrl);

            // Thiết lập headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", payosClientId);
            headers.set("x-api-key", payosApiKey);

            // Ghi log để debug
            System.out.println("PayOS Request URL: " + url);
            System.out.println("PayOS Request Headers: " + headers);
            System.out.println("PayOS Request Body: " + requestBody);

            // Gửi yêu cầu
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response;
            try {
                response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            } catch (HttpClientErrorException e) {
                System.err.println("PayOS Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                responseBody.put("error", "Lỗi khi gọi API PayOS: " + e.getResponseBodyAsString());
                return responseBody;
            }

            // Xử lý phản hồi
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("PayOS Response Body: " + response.getBody());
                Map<String, Object> responseData = response.getBody();
                String checkoutUrl = null;

                // Kiểm tra các trường có thể chứa link thanh toán
                if (responseData.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseData.get("data");
                    checkoutUrl = (String) data.get("checkoutUrl");
                    if (checkoutUrl == null) {
                        checkoutUrl = (String) data.get("paymentLink");
                    }
                } else {
                    checkoutUrl = (String) responseData.get("checkoutUrl");
                    if (checkoutUrl == null) {
                        checkoutUrl = (String) responseData.get("paymentLink");
                    }
                }

                if (checkoutUrl != null) {
                    responseBody.put("checkoutUrl", checkoutUrl);
                    responseBody.put("qrCodeUrl", "https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=" + checkoutUrl);
                    responseBody.put("orderCode", String.valueOf(orderCode));
                } else {
                    responseBody.put("error", "Không tìm thấy checkoutUrl hoặc paymentLink trong phản hồi từ PayOS");
                }
            } else {
                responseBody.put("error", "Lỗi khi tạo giao dịch: Phản hồi không hợp lệ từ PayOS");
            }

        } catch (Exception e) {
            System.err.println("PayOS Error: " + e.getMessage());
            e.printStackTrace();
            responseBody.put("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
        }

        return responseBody;
    }

    // API rút tiền - Xử lý yêu cầu rút tiền
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

        // Tìm giao dịch
        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

        // Lấy trạng thái mới từ DB
        Status status = Optional.ofNullable(statusRepository.findByName(newStatus))
            .orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái: " + newStatus));

        // Cập nhật trạng thái giao dịch
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        // Nếu giao dịch được duyệt, thực hiện rút tiền
        if ("SUCCESS".equals(newStatus)) {
            DebitWallet debitWallet = transaction.getDebitWallet();
            BigDecimal usdcAmount = transaction.getUsdcAmount();

            if (debitWallet.getBalance().compareTo(usdcAmount) < 0) {
                responseBody.put("error", "Số dư không đủ!");
                return responseBody;
            }

            // Nếu không chọn bankAccountId, lấy tài khoản ngân hàng mới nhất
            UserBankAccount bankAccount = userBankAccountRepository.findById(bankAccountId)
                    .orElseGet(() -> userBankAccountRepository.findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(
                        debitWallet.getUser().getId()
                    ).orElseThrow(() -> new RuntimeException("Người dùng chưa có tài khoản ngân hàng nào!")));

            // Trừ số dư USDC trong ví
            debitWallet.setBalance(debitWallet.getBalance().subtract(usdcAmount));
            debitWalletRepository.save(debitWallet);

            // Gửi yêu cầu rút tiền đến PayOS
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