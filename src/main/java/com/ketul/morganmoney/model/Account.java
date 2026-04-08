package com.ketul.morganmoney.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// @Entity tells JPA "this class should be saved to the database as a table"
// Each Account object becomes one row in the accounts table
@Entity
@Table(name = "accounts")
public class Account {

    // @Id marks this as the primary key - every row needs a unique identifier
    // @GeneratedValue means the database automatically assigns an ID number
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerName;
    private double cashBalance;
    private String currency;

    // @OneToMany means one Account can have many Holdings
    // cascade = ALL means if you save/delete an Account, its Holdings are saved/deleted too
    // orphanRemoval = true means if you remove a Holding from the list, it's deleted from the DB
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    private List<Holding> holdings = new ArrayList<>();

    // JPA needs an empty constructor to be able to recreate objects from the database
    public Account() {}

    public Account(String ownerName, double cashBalance) {
        this.ownerName = ownerName;
        this.cashBalance = cashBalance;
        this.currency = "USD";
    }

    public Account(String ownerName, double cashBalance, String currency) {
        this.ownerName = ownerName;
        this.cashBalance = cashBalance;
        this.currency = currency;
    }

    public String buy(String ticker, int quantity, double pricePerShare) {
        double totalCost = quantity * pricePerShare;

        if (totalCost > cashBalance) {
            return "Insufficient funds. You need " + currency + " " + totalCost + " but only have " + currency + " " + cashBalance;
        }

        cashBalance -= totalCost;

        Holding existing = findHolding(ticker);
        if (existing != null) {
            existing.addShares(quantity, pricePerShare);
        } else {
            holdings.add(new Holding(ticker, quantity, pricePerShare));
        }

        return "Bought " + quantity + " shares of " + ticker + " for " + currency + " " + totalCost + ". Remaining balance: " + currency + " " + cashBalance;
    }

    public String sell(String ticker, int quantity, double pricePerShare) {
        Holding existing = findHolding(ticker);
        if (existing == null) {
            return "You don't own any shares of " + ticker;
        }

        if (existing.getQuantity() < quantity) {
            return "You only own " + existing.getQuantity() + " shares of " + ticker + " but tried to sell " + quantity;
        }

        double proceeds = quantity * pricePerShare;
        cashBalance += proceeds;
        existing.removeShares(quantity);

        if (existing.getQuantity() == 0) {
            holdings.remove(existing);
        }

        return "Sold " + quantity + " shares of " + ticker + " for " + currency + " " + proceeds + ". New balance: " + currency + " " + cashBalance;
    }

    private Holding findHolding(String ticker) {
        for (Holding h : holdings) {
            if (h.getTicker().equals(ticker)) {
                return h;
            }
        }
        return null;
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public double getCashBalance() { return cashBalance; }
    public String getCurrency() { return currency; }
    public List<Holding> getHoldings() { return holdings; }
    public void setCashBalance(double cashBalance) { this.cashBalance = cashBalance; }
}
