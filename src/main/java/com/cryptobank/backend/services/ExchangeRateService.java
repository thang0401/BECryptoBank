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
	            Map<String, Map<String, Double>> response = restTemplate.getForObject(COINGECKO_API, Map.class);
	            return response.get("usd-coin").get("vnd");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
}
