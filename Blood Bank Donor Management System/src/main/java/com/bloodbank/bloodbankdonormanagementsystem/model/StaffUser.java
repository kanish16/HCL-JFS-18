package com.bloodbank.bloodbankdonormanagementsystem.model;

public class StaffUser extends AppUser {
    public StaffUser(String id, String username, String email) {
        super(id, username, email, "STAFF");
    }

    @Override
    public String getLandingModule() {
        return "M3: Blood Collection, Testing & Inventory";
    }
}