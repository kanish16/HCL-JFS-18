package com.bloodbank.bloodbankdonormanagementsystem.model;

import com.bloodbank.bloodbankdonormanagementsystem.BloodBankConstants;
import java.time.LocalDate;
import java.util.Objects;

public class BloodUnit extends BaseEntity {
    private static int unitCounter = 5000;

    private final String barcode;
    private final String donorId;
    private final String bloodGroup;
    private final String componentType;
    private final LocalDate collectionDate;
    private final LocalDate expiryDate;
    private String status; // AVAILABLE, RESERVED, ISSUED, EXPIRED

    // Chained Constructor 1 (Defaults collection date to today)
    public BloodUnit(String donorId, String bloodGroup, String componentType) {
        this(donorId, bloodGroup, componentType, LocalDate.now());
    }

    // Primary Constructor 2
    public BloodUnit(String donorId, String bloodGroup, String componentType, LocalDate collectionDate) {
        super("UNT-" + (++unitCounter));

        this.barcode = getId();
        this.donorId = Objects.requireNonNull(donorId, "Donor ID cannot be null");
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group cannot be null");
        this.componentType = Objects.requireNonNull(componentType, "Component type cannot be null");
        this.collectionDate = Objects.requireNonNull(collectionDate, "Collection date cannot be null");
        this.expiryDate = calculateExpiry(componentType, collectionDate);
        this.status = "AVAILABLE";
    }

    private static LocalDate calculateExpiry(String componentType, LocalDate collectionDate) {
        return switch (componentType.toUpperCase()) {
            case "PLATELETS" -> collectionDate.plusDays(BloodBankConstants.SHELF_LIFE_PLATELETS_DAYS);
            case "PRBC" -> collectionDate.plusDays(BloodBankConstants.SHELF_LIFE_PRBC_DAYS);
            case "FFP" -> collectionDate.plusDays(BloodBankConstants.SHELF_LIFE_FFP_DAYS);
            default -> collectionDate.plusDays(BloodBankConstants.SHELF_LIFE_WHOLE_BLOOD_DAYS);
        };
    }

    public boolean isExpiringWithin(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return !expiryDate.isBefore(LocalDate.now()) && !expiryDate.isAfter(threshold);
    }

    public String getBarcode() { return barcode; }
    public String getDonorId() { return donorId; }
    public String getBloodGroup() { return bloodGroup; }
    public String getComponentType() { return componentType; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = Objects.requireNonNull(status, "Status cannot be null"); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodUnit bloodUnit = (BloodUnit) o;
        return Objects.equals(barcode, bloodUnit.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }

    @Override
    public String toString() {
        return "BloodUnit{barcode='" + barcode + "', bloodGroup='" + bloodGroup + "', component='" + componentType + "', expiry=" + expiryDate + ", status='" + status + "'}";
    }
}