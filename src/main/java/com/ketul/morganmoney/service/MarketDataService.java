package com.ketul.morganmoney.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MarketDataService {

    private static final String API_KEY = "85BWBEKP0O9M4CHC";
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    public double getLivePrice(String ticker) {
        try {
            String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                + ticker + "&apikey=" + API_KEY;
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Stock response for " + ticker + ": " + response);
            JsonNode root = objectMapper.readTree(response);
            String price = root.path("Global Quote").path("05. price").asText();
            if (price == null || price.isEmpty()) return -1;
            return Double.parseDouble(price);
        } catch (Exception e) {
            System.out.println("Error fetching stock price for " + ticker + ": " + e.getMessage());
            return -1;
        }
    }

    public double getForexRate(String fromCurrency, String toCurrency) {
        try {
            String url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE"
                + "&from_currency=" + fromCurrency.trim()
                + "&to_currency=" + toCurrency.trim()
                + "&apikey=" + API_KEY;
            System.out.println("Calling forex URL: " + url);
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("Forex response: " + response);
            JsonNode root = objectMapper.readTree(response);
            String rate = root
                .path("Realtime Currency Exchange Rate")
                .path("5. Exchange Rate")
                .asText();
            System.out.println("Extracted rate: [" + rate + "]");
            if (rate == null || rate.isEmpty()) return -1;
            return Double.parseDouble(rate);
        } catch (Exception e) {
            System.out.println("Error fetching forex rate: " + e.getMessage());
            return -1;
        }
    }

    public double getPrice(String ticker) {
        if (ticker == null) return -1;
        String cleaned = ticker.trim();
        System.out.println("getPrice called for: [" + cleaned + "]");
        if (cleaned.contains("/")) {
            String[] parts = cleaned.split("/");
            if (parts.length == 2) {
                return getForexRate(parts[0], parts[1]);
            }
        }
        return getLivePrice(cleaned);
    }
}
