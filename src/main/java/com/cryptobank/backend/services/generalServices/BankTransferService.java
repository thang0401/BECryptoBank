package com.cryptobank.backend.services.generalServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class BankTransferService {

    @Value("${banking-api.base-url}")
    private String baseUrl;

    @Value("${banking-api.api-key}")
    private String apiKey;

    @Value("${banking-api.transfer-endpoint}")
    private String transferEndpoint;

    private final RestTemplate restTemplate;

    // Constructor để inject RestTemplate
    public BankTransferService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Phương thức chuyển tiền
    public String transferToBank(String accountNumber, Double amount) {
        String url = baseUrl + transferEndpoint;

        // Tạo request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountNumber", accountNumber);
        requestBody.put("amount", amount);

        // Thiết lập header với API key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Gửi yêu cầu POST
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Gửi yêu cầu chuyển tiền đến ngân hàng
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Kiểm tra phản hồi từ API ngân hàng
            if (response.getStatusCode().is2xxSuccessful()) {
                return "Chuyển tiền thành công!";
            } else {
                return "Lỗi khi chuyển tiền: " + response.getBody();
            }
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }
}
