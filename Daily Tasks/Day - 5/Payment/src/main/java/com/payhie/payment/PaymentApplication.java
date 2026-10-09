package com.payhie.payment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class PaymentApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=================================================");
        System.out.println("       DAY 5 HANDS-ON LAB: PAYMENT SYSTEM        ");
        System.out.println("=================================================\n");

        // Runtime Polymorphism: List of abstract base type Payment
        List<Payment> paymentQueue = new ArrayList<>();
        paymentQueue.add(new CardPayment(2500.0, "1234567890123456", "Karvendhan"));
        paymentQueue.add(new UpiPayment(850.0, "user@okaxis"));
        paymentQueue.add(new CashPayment(450.0, 500.0));

        // Process all payments via polymorphism
        for (Payment payment : paymentQueue) {
            payment.pay();

            // Demonstrate Interface checking (Card & UPI are Refundable, Cash is not)
            if (payment instanceof Refundable refundable) {
                refundable.refund(100.0);
            } else {
                System.out.println("[INFO] " + payment.getPaymentMethod() + " does not support electronic refund.");
            }
            System.out.println("-------------------------------------------------");
        }

        // Demonstrate Method Overloading
        System.out.println("\n--- Testing Method Overloading ---");
        Payment specialPayment = new UpiPayment(1200.0, "merchant@upi");
        specialPayment.pay("Registration Fee & Lunch Coupon");
    }
}