package com.bloodbank.bloodbankdonormanagementsystem.model;

public class HospitalUser extends AppUser {
    private final String hospitalName;

    public HospitalUser(String id, String username, String email, String hospitalName) {
        super(id, username, email, "HOSPITAL");
        this.hospitalName = hospitalName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    @Override
    public String getLandingModule() {
        return "M4: Hospital Blood Request & Issue Tracking";
    }
}