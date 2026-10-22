package com.bankacc.bankaccount.service;

import com.bankacc.bankaccount.model.BankAccount;

public class BankAccountService {

    public void deposit(BankAccount account, double amount) {
        account.deposit(amount);
    }

    public boolean withdraw(BankAccount account, double amount) {
        return account.withdraw(amount);
    }
}