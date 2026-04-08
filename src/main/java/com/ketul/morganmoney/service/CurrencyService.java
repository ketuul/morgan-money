package com.ketul.morganmoney.service;

import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    // Takes a location string and returns the appropriate currency code
    public String getCurrencyForLocation(String location) {

        // Convert to uppercase so "usa", "USA", and "Usa" all work
        switch (location.toUpperCase()) {
            case "USA":
            case "UNITED STATES":
                return "USD";
            case "UK":
            case "UNITED KINGDOM":
            case "BRITAIN":
                return "GBP";
            case "EUROPE":
            case "GERMANY":
            case "FRANCE":
            case "ITALY":
            case "SPAIN":
                return "EUR";
            case "JAPAN":
                return "JPY";
            case "INDIA":
                return "INR";
            case "CANADA":
                return "CAD";
            case "AUSTRALIA":
                return "AUD";
            case "CHINA":
                return "CNY";
            case "SWITZERLAND":
                return "CHF";
            default:
                // If we don't recognize the location, default to USD
                return "USD";
        }
    }
}
