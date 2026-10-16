package com.bloodbank.bloodbankdonormanagementsystem.model;

import java.time.LocalDate;
import java.util.Objects;

public class DonationCamp extends BaseEntity {
    private static int campCounter = 9000;

    private final String campId;
    private String campName;
    private String venue;
    private LocalDate campDate;
    private int estimatedDonors;
    private int unitsCollected;
    private String status; // UPCOMING, ACTIVE, CONCLUDED

    public DonationCamp(String campName, String venue, LocalDate campDate, int estimatedDonors) {
        super("CMP-" + (++campCounter));

        if (campName == null || campName.trim().isEmpty()) {
            throw new IllegalArgumentException("Camp name cannot be blank");
        }
        if (venue == null || venue.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue cannot be blank");
        }
        if (estimatedDonors <= 0) {
            throw new IllegalArgumentException("Estimated donors must be positive");
        }

        this.campId = getId();
        this.campName = campName.trim();
        this.venue = venue.trim();
        this.campDate = Objects.requireNonNull(campDate, "Camp date cannot be null");
        this.estimatedDonors = estimatedDonors;
        this.unitsCollected = 0;
        this.status = campDate.isBefore(LocalDate.now()) ? "CONCLUDED" : "UPCOMING";
    }

    public String getCampId() { return campId; }
    public String getCampName() { return campName; }
    public void setCampName(String campName) { this.campName = campName; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public LocalDate getCampDate() { return campDate; }
    public void setCampDate(LocalDate campDate) { this.campDate = campDate; }
    public int getEstimatedDonors() { return estimatedDonors; }
    public void setEstimatedDonors(int estimatedDonors) { this.estimatedDonors = estimatedDonors; }
    public int getUnitsCollected() { return unitsCollected; }
    public void setUnitsCollected(int unitsCollected) { this.unitsCollected = unitsCollected; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonationCamp that = (DonationCamp) o;
        return Objects.equals(campId, that.campId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campId);
    }

    @Override
    public String toString() {
        return "DonationCamp{id='" + campId + "', name='" + campName + "', date=" + campDate + ", status='" + status + "'}";
    }
}