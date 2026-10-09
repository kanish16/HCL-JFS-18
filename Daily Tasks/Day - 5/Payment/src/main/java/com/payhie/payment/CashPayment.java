package com.payhie.payment;

public class CashPayment extends Payment {
    private final double tenderedAmount;

    public CashPayment(double amount, double tenderedAmount) {
        super(amount);
        if (tenderedAmount < amount) {
            throw new IllegalArgumentException("Tendered cash cannot be less than the billing amount.");
        }
        this.tenderedAmount = tenderedAmount;
    }

    @Override
    public boolean processPayment() {
        double change = tenderedAmount - getAmount();
        System.out.println("Received Cash: ₹" + tenderedAmount + ". Returning change: ₹" + change);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "CASH";
    }
}