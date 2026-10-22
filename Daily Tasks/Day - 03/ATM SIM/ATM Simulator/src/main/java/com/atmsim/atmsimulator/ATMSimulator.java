package com.atmsim.atmsimulator;

import java.util.ArrayList;
import java.util.Scanner;

public class ATMSimulator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        final int CORRECT_PIN = 1234;
        final int MAX_ATTEMPTS = 3;

        int attempts = 0;
        boolean authenticated = false;

        // -------------------------
        // PIN Authentication
        // -------------------------
        while (attempts < MAX_ATTEMPTS) {

            System.out.print("Enter your PIN: ");
            int pin = scanner.nextInt();

            if (pin == CORRECT_PIN) {
                authenticated = true;
                System.out.println("PIN verified successfully!");
                break;
            }

            attempts++;

            System.out.println("Incorrect PIN!");

            if (attempts < MAX_ATTEMPTS) {
                System.out.println("Attempts remaining: "
                        + (MAX_ATTEMPTS - attempts));
                continue;
            }

            System.out.println("Maximum attempts exceeded.");
        }

        // Stop if authentication failed
        if (!authenticated) {
            System.out.println("Account locked. Exiting...");
            scanner.close();
            return;
        }

        // -------------------------
        // ATM Variables
        // -------------------------
        double balance = 10000.0;

        ArrayList<String> transactions = new ArrayList<>();

        transactions.add("Initial Balance: ₹" + balance);

        int choice;

        // -------------------------
        // ATM Menu
        // -------------------------
        do {

            System.out.println("\n===== ATM MENU =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Mini Statement");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    // Check Balance
                    System.out.println("Current Balance: ₹" + balance);
                    break;

                case 2:
                    // Deposit
                    System.out.print("Enter deposit amount: ₹");
                    double deposit = scanner.nextDouble();

                    if (deposit <= 0) {
                        System.out.println(
                                "Invalid amount. Deposit must be greater than 0."
                        );
                        continue;
                    }

                    balance += deposit;

                    transactions.add("Deposit: ₹" + deposit);

                    System.out.println("Deposit successful!");
                    System.out.println("Updated Balance: ₹" + balance);
                    break;

                case 3:
                    // Withdraw
                    System.out.print("Enter withdrawal amount: ₹");
                    double withdrawal = scanner.nextDouble();

                    if (withdrawal <= 0) {
                        System.out.println(
                                "Invalid amount. Withdrawal must be greater than 0."
                        );
                        continue;
                    }

                    if (withdrawal > balance) {
                        System.out.println("Insufficient balance!");
                        continue;
                    }

                    balance -= withdrawal;

                    transactions.add("Withdrawal: ₹" + withdrawal);

                    System.out.println("Withdrawal successful!");
                    System.out.println("Remaining Balance: ₹" + balance);
                    break;

                case 4:
                    // Mini Statement
                    System.out.println("\n===== MINI STATEMENT =====");

                    for (String transaction : transactions) {
                        System.out.println(transaction);
                    }

                    System.out.println("--------------------------");
                    System.out.println("Current Balance: ₹" + balance);
                    break;

                case 5:
                    // Exit
                    System.out.println("Thank you for using the ATM!");
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please select 1-5."
                    );
                    continue;
            }

        } while (choice != 5);

        scanner.close();
    }
}