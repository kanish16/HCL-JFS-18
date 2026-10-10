package com.bloodbank.bloodbankdonormanagementsystem.exception;

public class BloodBankException extends RuntimeException {
    public BloodBankException(String message) {
        super(message);
    }

    public BloodBankException(String message, Throwable cause) {
        super(message, cause);
    }
}