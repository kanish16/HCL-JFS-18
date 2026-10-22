package com.bloodbank.bloodbankdonormanagementsystem.model;

import com.bloodbank.bloodbankdonormanagementsystem.BloodBankConstants;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class BloodUnit extends BaseEntity {
    private static int unitCounter = 5000;

    // Functional dispatch table replacing if-else ladder for expiry calculation
    private static final Map<String, UnaryOperator<LocalDate>> EXPIRY_CALCULATORS = Map.of(
            "PLATELETS", date -> date.plusDays(BloodBankConstants.SHELF_LIFE_PLATELETS_DAYS),
            "PRBC", date -> date.plusDays(BloodBankConstants.SHELF_LIFE_PRBC_DAYS),
            "FFP", date -> date.plusDays(BloodBankConstants.SHELF_LIFE_FFP_DAYS),
            "WHOLE_BLOOD", date -> date.plusDays(BloodBankConstants.SHELF_LIFE_WHOLE_BLOOD_DAYS)
    );

    private final String barcode;
    private final String donorId;
    private final String bloodGroup;
    private final String componentType;
    private final LocalDate collectionDate;
    private final LocalDate expiryDate;
    private String status;

    public BloodUnit(String donorId, String bloodGroup, String componentType) {
        this(donorId, bloodGroup, componentType, LocalDate.now());
    }

    public BloodUnit(String donorId, String bloodGroup, String componentType, LocalDate collectionDate) {
        super("UNT-" + (++unitCounter));

        this.barcode = getId();
        this.donorId = Objects.requireNonNull(donorId, "Donor ID cannot be null");
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group cannot be null");
        this.componentType = Objects.requireNonNull(componentType, "Component type cannot be null").toUpperCase();
        this.collectionDate = Objects.requireNonNull(collectionDate, "Collection date cannot be null");

        // Evaluated via functional lambda lookup:
        this.expiryDate = EXPIRY_CALCULATORS
                .getOrDefault(this.componentType, date -> date.plusDays(BloodBankConstants.SHELF_LIFE_WHOLE_BLOOD_DAYS))
                .apply(collectionDate);

        this.status = "AVAILABLE";
    }

    public String getBarcode() { return barcode; }
    public String getDonorId() { return donorId; }
    public String getBloodGroup() { return bloodGroup; }
    public String getComponentType() { return componentType; }
    public LocalDate getCollectionDate() { return collectionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = Objects.requireNonNull(status); }
}