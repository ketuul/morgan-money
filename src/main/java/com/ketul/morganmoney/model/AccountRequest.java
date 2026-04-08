package com.ketul.morganmoney.model;

// This class represents the data the user sends when creating an account
// It is called a "request body" — the user sends this as JSON to your API
public class AccountRequest {

    private String name;
    private String location; // e.g. "USA", "UK", "Japan", "India"
    private double startingBalance;

    // Spring needs an empty constructor to be able to read the incoming JSON
    public AccountRequest() {}

    public AccountRequest(String name, String location, double startingBalance) {
        this.name = name;
        this.location = location;
        this.startingBalance = startingBalance;
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public double getStartingBalance() { return startingBalance; }
}
