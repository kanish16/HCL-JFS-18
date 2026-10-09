package com.payhie.payment;

public class CardPayment extends Payment implements Refundable {
    private final String cardNumber;
    private final String cardHolderName;

    public CardPayment(double amount, String cardNumber, String cardHolderName) {
        super(amount);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public boolean processPayment() {
        String maskedCard = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("Authorizing card payment for " + cardHolderName + " (" + maskedCard + ")...");
        return true;
    }

    @Override
    public boolean refund(double amount) {
        System.out.println("Refund of ₹" + amount + " credited back to Card ending in "
                + cardNumber.substring(cardNumber.length() - 4));
        return true;
    }

    @Override
    public String getPaymentMethod() {
        return "CREDIT/DEBIT CARD";
    }
}