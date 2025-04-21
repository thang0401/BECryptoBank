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
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
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
import vn.payos.*;
import vn.payos.exception.PayOSException;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

@Service
public class BankTransferService2 {

	@Value("${payos.base-url}")
	private String payosBaseUrl;

	@Value("${payos.client-id}")
	private String payosClientId;

	@Value("${payos.api-key}")
	private String payosApiKey;

	@Value("${payos.checksum-key}")
	private String checksumKey;

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
	public Map<String, String> depositToPayOS(String orderId, Double amount, String description, String returnUrl,
			String cancelUrl, String userId) {
		Map<String, String> responseBody = new HashMap<>();

		try {
			// Validate inputs
			if (amount == null || amount <= 0) {
				responseBody.put("error", "amount phải lớn hơn 0");
				return responseBody;
			}
			if (description == null || description.trim().isEmpty()) {
				description = "" + userId; // Nhúng user_id vào description
			} else {
				description = description + "" + userId; // Thêm user_id vào description
			}
			if (returnUrl == null || returnUrl.trim().isEmpty()) {
				responseBody.put("error", "returnUrl không được để trống");
				return responseBody;
			}
			if (cancelUrl == null || cancelUrl.trim().isEmpty()) {
				responseBody.put("error", "cancelUrl không được để trống");
				return responseBody;
			}

			// Generate unique orderCode
			Integer orderCode;
			if ("auto".equalsIgnoreCase(orderId)) {
				String uniqueId = System.currentTimeMillis() + String.valueOf(new Random().nextInt(10000));
				orderCode = Integer.parseInt(uniqueId.substring(uniqueId.length() - 9));
			} else {
				try {
					orderCode = Integer.parseInt(orderId);
				} catch (NumberFormatException e) {
					String uniqueId = System.currentTimeMillis() + String.valueOf(new Random().nextInt(10000));
					orderCode = Integer.parseInt(uniqueId.substring(uniqueId.length() - 9));
				}
			}

			// Initialize PayOS
			final PayOS payos = new PayOS(payosClientId, payosApiKey, checksumKey);

			// Build PaymentData
			PaymentData paymentData = PaymentData.builder().orderCode(Long.parseLong(orderCode.toString()))
					.amount(amount.intValue()).description(description) // Sử dụng description có user_id
					.returnUrl(returnUrl).cancelUrl(cancelUrl).build();

			// Log for debugging
			System.out.println("PayOS Client ID: " + payosClientId);
			System.out.println("PaymentData: " + paymentData);

			// Call PayOS API
			CheckoutResponseData result = payos.createPaymentLink(paymentData);

			// Process response
			responseBody.put("checkoutUrl", result.getCheckoutUrl());
			responseBody.put("qrCodeUrl",
					"https://chart.googleapis.com/chart?chs=250x250&cht=qr&chl=" + result.getCheckoutUrl());
			responseBody.put("orderCode", String.valueOf(orderCode));

		} catch (PayOSException e) {
			System.err.println("PayOS Error: " + e.getMessage());
			e.printStackTrace();
			responseBody.put("error", "Lỗi khi gọi API PayOS: " + e.getMessage());
			if (e.getMessage().contains("231") || e.getMessage().contains("Đơn thanh toán đã tồn tại")) {
				responseBody.put("error", "Đơn thanh toán đã tồn tại. Vui lòng sử dụng orderCode khác.");
			}
		} catch (Exception e) {
			System.err.println("Unexpected Error: " + e.getMessage());
			e.printStackTrace();
			responseBody.put("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
		}

		return responseBody;
	}

	// API rút tiền - Xử lý yêu cầu rút tiền
	@Transactional
	public Map<String, String> requestWithdraw(String userId, BigDecimal usdcAmount) {
		Map<String, String> responseBody = new HashMap<>();

		// Kiểm tra số dư USDC của user
		DebitWallet debitWallet = debitWalletRepository.findByUserId(userId).stream().findFirst()
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
		Status pendingStatus = Optional.ofNullable(statusRepository.findById("cvvveejme6nnaun2s4a0"))
				.orElseThrow(() -> new RuntimeException("Không tìm thấy trạng thái PENDING")).get();

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
    	 try {
    		 	// Tìm giao dịch
    	        UsdcVndTransaction transaction = transactionRepository.findById(transactionId)
    	            .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + transactionId));

    	        // Lấy trạng thái mới từ DB
    	        Status status = statusRepository.findByName(newStatus)
    	        		.stream()
    	        		.findFirst()
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
    	            	    .orElseGet(() -> userBankAccountRepository
    	            	        .findFirstByUserIdOrderByUpdatedAtDescCreatedAtDesc(debitWallet.getUser().getId())
    	            	        .orElseThrow(() -> new RuntimeException("Người dùng chưa có tài khoản ngân hàng nào!"))
    	            	    );


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

    	        
 		} catch (Exception e) {
 			responseBody.put("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
 		}
        return responseBody;
    }
}