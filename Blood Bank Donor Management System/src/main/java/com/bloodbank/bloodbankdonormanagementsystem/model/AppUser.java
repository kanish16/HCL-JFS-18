package com.bloodbank.bloodbankdonormanagementsystem.model;

import java.util.Objects;

public abstract class AppUser extends BaseEntity {
    private String username;
    private String email;
    private final String role;

    public AppUser(String id, String username, String email, String role) {
        super(id);
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
    }

    // Abstract method: Each role provides its designated landing view
    public abstract String getLandingModule();

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
}