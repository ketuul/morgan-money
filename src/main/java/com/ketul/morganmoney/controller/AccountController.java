package com.ketul.morganmoney.controller;

import com.ketul.morganmoney.model.Account;
import com.ketul.morganmoney.model.AccountRequest;
import com.ketul.morganmoney.model.Holding;
import com.ketul.morganmoney.repository.AccountRepository;
import com.ketul.morganmoney.service.MarketDataService;
import com.ketul.morganmoney.service.CurrencyService;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountController {

    private AccountRepository accountRepository;
    private MarketDataService marketDataService;
    private CurrencyService currencyService;

    public AccountController(AccountRepository accountRepository,
                             MarketDataService marketDataService,
                             CurrencyService currencyService) {
        this.accountRepository = accountRepository;
        this.marketDataService = marketDataService;
        this.currencyService = currencyService;
    }

    @PostMapping("/account")
    public String createAccount(@RequestBody AccountRequest request) {
        String currency = currencyService.getCurrencyForLocation(request.getLocation());
        Account account = new Account(request.getName(), request.getStartingBalance(), currency);
        accountRepository.save(account);
        return "Account created for " + request.getName() + " in " + request.getLocation()
            + " with " + currency + " " + request.getStartingBalance() + ". Welcome to Morgan Money! Your account ID is: " + account.getId();
    }

    @GetMapping("/portfolio/{accountId}")
    public Object getPortfolio(@PathVariable Long accountId) {
        Optional<Account> optional = accountRepository.findById(accountId);
        if (optional.isEmpty()) {
            return "No account found with ID " + accountId;
        }

        Account account = optional.get();

        for (Holding holding : account.getHoldings()) {
            double livePrice = marketDataService.getPrice(holding.getTicker());
            if (livePrice != -1) {
                holding.calculateProfitLoss(livePrice);
            }
            // Wait 1.5 seconds between calls — Alpha Vantage free tier limit
            try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        return account;
    }

    @GetMapping("/buy/{accountId}")
    public String buy(
        @PathVariable Long accountId,
        @RequestParam String ticker,
        @RequestParam int quantity
    ) {
        Optional<Account> optional = accountRepository.findById(accountId);
        if (optional.isEmpty()) {
            return "No account found with ID " + accountId;
        }
        double livePrice = marketDataService.getPrice(ticker);
        if (livePrice == -1) {
            return "Could not fetch live price for " + ticker;
        }
        Account account = optional.get();
        String result = account.buy(ticker, quantity, livePrice);
        accountRepository.save(account);
        return result;
    }

    @GetMapping("/sell/{accountId}")
    public String sell(
        @PathVariable Long accountId,
        @RequestParam String ticker,
        @RequestParam int quantity
    ) {
        Optional<Account> optional = accountRepository.findById(accountId);
        if (optional.isEmpty()) {
            return "No account found with ID " + accountId;
        }
        double livePrice = marketDataService.getPrice(ticker);
        if (livePrice == -1) {
            return "Could not fetch live price for " + ticker;
        }
        Account account = optional.get();
        String result = account.sell(ticker, quantity, livePrice);
        accountRepository.save(account);
        return result;
    }

    @GetMapping("/price")
    public String getPrice(@RequestParam String ticker) {
        double livePrice = marketDataService.getPrice(ticker);
        if (livePrice == -1) {
            return "Could not fetch price for " + ticker;
        }
        return ticker + " is currently trading at " + livePrice;
    }
}
