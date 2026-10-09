package com.payhie.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Payment {
    private final String transactionId;
    private final double amount;
    private final LocalDateTime timestamp;
    private String status;

    public Payment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        this.transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Abstract method: To be overridden by subclasses (Runtime Polymorphism)
    public abstract boolean processPayment();

    public abstract String getPaymentMethod();

    // Overloaded Method 1: Standard payment
    public void pay() {
        if (processPayment()) {
            this.status = "SUCCESS";
            System.out.println("[SUCCESS] Paid ₹" + amount + " using " + getPaymentMethod() + " | Txn ID: " + transactionId);
        } else {
            this.status = "FAILED";
            System.out.println("[FAILED] Payment failed for " + getPaymentMethod() + " | Txn ID: " + transactionId);
        }
    }

    // Overloaded Method 2: Payment with note
    public void pay(String note) {
        System.out.println("Processing note: \"" + note + "\"");
        pay();
    }

    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}