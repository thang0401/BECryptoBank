package com.cryptobank.backend.services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {
	 private final RestTemplate restTemplate = new RestTemplate();
	    private static final String COINGECKO_API = "https://api.coingecko.com/api/v3/simple/price?ids=usd-coin&vs_currencies=vnd";

	    public Double getUsdcVndRate() {
	        try {
	            Map<String, Map<String, Object>> response = restTemplate.getForObject(COINGECKO_API, Map.class);
	            
	            if (response != null && response.containsKey("usd-coin") && response.get("usd-coin").containsKey("vnd")) {
	                // Lấy giá trị tỷ giá, chuyển đổi thành Double nếu cần
	                Object vndValue = response.get("usd-coin").get("vnd");
	                
	                if (vndValue instanceof Double) {
	                    return (Double) vndValue;
	                } else if (vndValue instanceof Integer) {
	                    // Nếu tỷ giá là Integer, chuyển đổi nó thành Double
	                    return ((Integer) vndValue).doubleValue();
	                } else {
	                    System.err.println("Dữ liệu tỷ giá không hợp lệ: " + vndValue.getClass().getName());
	                    return null;
	                }
	            } else {
	                System.err.println("Không nhận được dữ liệu tỷ giá USD Coin");
	                return 26000.0;  // Giá trị mặc định
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }


}
