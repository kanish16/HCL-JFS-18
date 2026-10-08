package com.bankacc.bankaccount.app;

import com.bankacc.bankaccount.model.BankAccount;
import com.bankacc.bankaccount.service.BankAccountService;

public class BankAccountApp {

    public static void main(String[] args) {

        BankAccountService service = new BankAccountService();

        BankAccount account = new BankAccount("Kanishkar");

        service.deposit(account, 1000);

        System.out.println("Account Number: " + account.getAccountNumber());
        System.out.println("Account Holder: " + account.getAccountHolder());
        System.out.println("Balance: " + account.getBalance());

        boolean result = service.withdraw(account, 1000);

        System.out.println("Withdrawal successful: " + result);
        System.out.println("Balance after withdrawal: "
                + account.getBalance());

        System.out.println("Total accounts: "
                + BankAccount.getAccountCount());
    }
}