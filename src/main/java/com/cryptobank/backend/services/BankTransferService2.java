package com.cryptobank.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class BankTransferService2 {

    @Value("${payos.base-url}")
    private String payosBaseUrl;

    @Value("${payos.client-id}")
    private String payosClientId;

    @Value("${payos.api-key}")
    private String payosApiKey;

    private final RestTemplate restTemplate;
    private final UserService userService; // Service để kiểm tra số dư USDC

    public BankTransferService2(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    // 1️⃣ API nạp tiền - Trả về mã QR
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

    // 2️⃣ API rút tiền - Xử lý yêu cầu rút tiền
    public Map<String, String> withdrawFromPayOS(String userId, Double amount, String bankAccount, String bankCode) {
        // Kiểm tra số dư USDC của user
        Double userBalance = userService.getUserBalance(userId);
        Map<String, String> responseBody = new HashMap<>();

        if (userBalance < amount) {
            responseBody.put("error", "Số dư không đủ!");
            return responseBody;
        }

        String url = payosBaseUrl + "/v2/withdraw";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("clientId", payosClientId);
        requestBody.put("amount", amount);
        requestBody.put("bankAccount", bankAccount);
        requestBody.put("bankCode", bankCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + payosApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // Giảm số dư USDC của user sau khi rút tiền thành công
            userService.decreaseUserBalance(userId, amount);
            responseBody.put("message", "Rút tiền thành công!");
        } else {
            responseBody.put("error", "Lỗi khi rút tiền!");
        }
        return responseBody;
    }
}
