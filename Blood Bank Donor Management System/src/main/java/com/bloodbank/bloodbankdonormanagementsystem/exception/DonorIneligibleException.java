package com.bloodbank.bloodbankdonormanagementsystem.exception;

import java.time.LocalDate;

public class DonorIneligibleException extends BloodBankException {
    private final LocalDate nextEligibleDate;

    public DonorIneligibleException(String message, LocalDate nextEligibleDate) {
        super(message);
        this.nextEligibleDate = nextEligibleDate;
    }

    public LocalDate getNextEligibleDate() {
        return nextEligibleDate;
    }
}