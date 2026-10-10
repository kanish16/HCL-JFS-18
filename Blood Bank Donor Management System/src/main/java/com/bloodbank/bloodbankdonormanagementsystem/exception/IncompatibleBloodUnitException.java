package com.bloodbank.bloodbankdonormanagementsystem.exception;

public class IncompatibleBloodUnitException extends BloodBankException {
    private final String donorBloodGroup;
    private final String recipientBloodGroup;

    public IncompatibleBloodUnitException(String donorBloodGroup, String recipientBloodGroup) {
        super("Transfusion mismatch: Donor blood group '" + donorBloodGroup
                + "' is incompatible with recipient blood group '" + recipientBloodGroup + "'.");
        this.donorBloodGroup = donorBloodGroup;
        this.recipientBloodGroup = recipientBloodGroup;
    }

    public String getDonorBloodGroup() {
        return donorBloodGroup;
    }

    public String getRecipientBloodGroup() {
        return recipientBloodGroup;
    }
}