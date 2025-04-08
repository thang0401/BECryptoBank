package com.cryptobank.backend.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;



@Service
public class PayOSService {
    private final String payosBaseUrl = "https://api.payos.vn"; // URL của PayOS
    private final String payosClientId = "your-client-id"; // Client ID của bạn
    private final String payosApiKey = "your-api-key"; // API Key của bạn

    private final RestTemplate restTemplate;

    @Autowired
    public PayOSService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> withdraw(BigDecimal amount, String bankAccount, String bankCode) {
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

        Map<String, String> responseBody = new HashMap<>();
        if (response.getStatusCode().is2xxSuccessful()) {
            responseBody.put("message", "Yêu cầu rút tiền thành công!");
        } else {
            responseBody.put("error", "Lỗi khi gửi yêu cầu rút tiền!");
        }
        return responseBody;
    }
}
