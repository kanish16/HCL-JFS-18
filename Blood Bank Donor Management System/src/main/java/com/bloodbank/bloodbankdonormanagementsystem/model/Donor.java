package com.bloodbank.bloodbankdonormanagementsystem.model;

import com.bloodbank.bloodbankdonormanagementsystem.BloodBankConstants;
import com.bloodbank.bloodbankdonormanagementsystem.exception.DonorIneligibleException;
import com.bloodbank.bloodbankdonormanagementsystem.exception.IncompatibleBloodUnitException;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;

import java.time.LocalDate;
import java.util.Objects;

public class Donor {
    private static int idCounter = 1000;

    private final String donorId;
    private String name;
    private int age;
    private double weightKg;
    private String bloodGroup;
    private String phone;
    private LocalDate lastDonationDate;

    // Chained Constructor 1 (First-time donor)
    public Donor(String name, int age, double weightKg, String bloodGroup, String phone) {
        this(name, age, weightKg, bloodGroup, phone, null);
    }

    public static void validateCompatibility(CompatibilityStrategy strategy, String donorGroup, String recipientGroup) {
        if (!strategy.getCompatibleDonorGroups(recipientGroup).contains(donorGroup)) {
            throw new IncompatibleBloodUnitException(donorGroup, recipientGroup);
        }
    }

    public void validateEligibilityForDonation() {
        if (this.getAge() < BloodBankConstants.MIN_DONOR_AGE || this.getAge() > BloodBankConstants.MAX_DONOR_AGE) {
            throw new DonorIneligibleException(
                    "Donor ineligible: Age must be between " + BloodBankConstants.MIN_DONOR_AGE
                            + " and " + BloodBankConstants.MAX_DONOR_AGE + " years.", null);
        }
        if (this.getWeightKg() < BloodBankConstants.MIN_DONOR_WEIGHT_KG) {
            throw new DonorIneligibleException(
                    "Donor ineligible: Weight must be at least " + BloodBankConstants.MIN_DONOR_WEIGHT_KG + " kg.", null);
        }
        if (!this.isEligibleToDonate()) {
            throw new DonorIneligibleException(
                    "Donor is in cooling period until " + this.getNextEligibleDate(), this.getNextEligibleDate());
        }
    }

    // Chained Constructor 2 (Full parameters with validations)
    public Donor(String name, int age, double weightKg, String bloodGroup, String phone, LocalDate lastDonationDate) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor name cannot be blank");
        }
        if (age < BloodBankConstants.MIN_DONOR_AGE || age > BloodBankConstants.MAX_DONOR_AGE) {
            throw new IllegalArgumentException("Donor age must be between "
                    + BloodBankConstants.MIN_DONOR_AGE + " and " + BloodBankConstants.MAX_DONOR_AGE);
        }
        if (weightKg < BloodBankConstants.MIN_DONOR_WEIGHT_KG) {
            throw new IllegalArgumentException("Donor weight must be at least "
                    + BloodBankConstants.MIN_DONOR_WEIGHT_KG + " kg");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }

        this.donorId = "DNR-" + (++idCounter);
        this.name = name.trim();
        this.age = age;
        this.weightKg = weightKg;
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group cannot be null");
        this.phone = phone.trim();
        this.lastDonationDate = lastDonationDate;
    }

    // FR1: Business logic for eligibility cooldown (90 days)
    public boolean isEligibleToDonate() {
        if (lastDonationDate == null) {
            return true;
        }
        LocalDate nextEligibleDate = lastDonationDate.plusDays(BloodBankConstants.WHOLE_BLOOD_COOLDOWN_DAYS);
        return !LocalDate.now().isBefore(nextEligibleDate);
    }

    public LocalDate getNextEligibleDate() {
        if (lastDonationDate == null) {
            return LocalDate.now();
        }
        return lastDonationDate.plusDays(BloodBankConstants.WHOLE_BLOOD_COOLDOWN_DAYS);
    }

    // Getters and Setters
    public String getDonorId() { return donorId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.requireNonNull(name, "Name cannot be null"); }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group cannot be null"); }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = Objects.requireNonNull(phone, "Phone cannot be null"); }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return Objects.equals(donorId, donor.donorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donorId);
    }

    @Override
    public String toString() {
        return "Donor{id='" + donorId + "', name='" + name + "', bloodGroup='" + bloodGroup + "', eligible=" + isEligibleToDonate() + "}";
    }
}