package com.payhie.payment;

public class UpiPayment extends Payment implements Refundable {
    private final String upiId;

    public UpiPayment(double amount, String upiId) {
        super(amount);
        this.upiId = upiId;
    }

    @Override
    public boolean processPayment() {
        System.out.println("Verifying VPA and completing UPI transfer via: " + upiId + "...");
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("Instant refund of ₹" + amount + " sent to UPI ID: " + upiId);
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "UPI";
    }
}