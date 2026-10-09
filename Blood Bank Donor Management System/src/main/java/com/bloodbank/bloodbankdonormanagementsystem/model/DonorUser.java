package com.bloodbank.bloodbankdonormanagementsystem.model;

public class DonorUser extends AppUser {
    private final String donorReferenceId;

    public DonorUser(String id, String username, String email, String donorReferenceId) {
        super(id, username, email, "DONOR");
        this.donorReferenceId = donorReferenceId;
    }

    public String getDonorReferenceId() {
        return donorReferenceId;
    }

    @Override
    public String getLandingModule() {
        return "M2: Donation Camp Booking & Eligibility Tracker";
    }
}