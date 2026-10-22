package com.bankacc.bankaccount.model;

public class BankAccount {

    private String accountNumber;
    private String accountHolder;
    private double balance;

    private static int accountCount = 0;

    public BankAccount() {
        this("UNKNOWN");
    }

    public BankAccount(String accountHolder) {
        this("ACC-" + (accountCount + 1), accountHolder);
    }

    public BankAccount(String accountNumber, String accountHolder) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = 0.0;
        accountCount++;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public double getBalance() {
        return balance;
    }

    public static int getAccountCount() {
        return accountCount;
    }

    public void deposit(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        balance += amount;
    }

    public boolean withdraw(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        // INTENTIONAL BUG — do not fix this yet
        if (amount < balance) {
            balance -= amount;
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof BankAccount other)) {
            return false;
        }

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}