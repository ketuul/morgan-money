package com.ketul.morganmoney.model;

import jakarta.persistence.*;

@Entity
@Table(name = "holdings")
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private int quantity;
    private double averageBuyPrice;

    @Transient
    private double currentPrice;

    @Transient
    private double profitLoss;

    @Transient
    private double profitLossPercent;

    public Holding() {}

    public Holding(String ticker, int quantity, double averageBuyPrice) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }

    public void addShares(int newQuantity, double newPrice) {
        double totalCost = (this.quantity * this.averageBuyPrice) + (newQuantity * newPrice);
        this.quantity += newQuantity;
        this.averageBuyPrice = totalCost / this.quantity;
    }

    public void removeShares(int quantityToSell) {
        this.quantity -= quantityToSell;
    }

    public void calculateProfitLoss(double currentPrice) {
        this.currentPrice = currentPrice;
        double totalCurrentValue = currentPrice * quantity;
        double totalCostBasis = averageBuyPrice * quantity;
        this.profitLoss = totalCurrentValue - totalCostBasis;
        this.profitLossPercent = ((currentPrice - averageBuyPrice) / averageBuyPrice) * 100;
    }

    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public int getQuantity() { return quantity; }
    public double getAverageBuyPrice() { return averageBuyPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public double getProfitLoss() { return profitLoss; }
    public double getProfitLossPercent() { return profitLossPercent; }
}
