package com.bloodbank.bloodbankdonormanagementsystem.model;

public class AdminUser extends AppUser {
    public AdminUser(String id, String username, String email) {
        super(id, username, email, "ADMIN");
    }

    @Override
    public String getLandingModule() {
        return "M5: Stock Dashboard & System Administration";
    }
}