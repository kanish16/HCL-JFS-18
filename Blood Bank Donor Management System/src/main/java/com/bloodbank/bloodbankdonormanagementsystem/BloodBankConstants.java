package com.bloodbank.bloodbankdonormanagementsystem;

public final class BloodBankConstants {
    // Private Constructor
    private BloodBankConstants() {}

        // Donor Eligibility
        public static final int MIN_DONOR_AGE = 18;
        public static final int MAX_DONOR_AGE = 65;
        public static final double MIN_DONOR_WEIGHT_KG = 50.0;
        public static final double MIN_HEMOGLOBIN_G_DL = 12.5;
        public static final int WHOLE_BLOOD_COOLDOWN_DAYS = 90;

        // Blood Component Shelf Life (in days)
        public static final int SHELF_LIFE_WHOLE_BLOOD_DAYS = 35;
        public static final int SHELF_LIFE_PRBC_DAYS = 42;       // Packed Red Blood Cells
        public static final int SHELF_LIFE_PLATELETS_DAYS = 5;
        public static final int SHELF_LIFE_FFP_DAYS = 365;       // Fresh Frozen Plasma

        // Expiry and Alert Thresholds
        public static final int EXPIRY_ALERT_WINDOW_DAYS = 3;    // Flag units expiring within 3 days

        // Standard Blood Groups
        public static final String[] BLOOD_GROUPS = {
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        };

        // Component Types
        public static final String[] COMPONENT_TYPES = {
                "WHOLE_BLOOD", "PRBC", "PLATELETS", "FFP"
        };

        // Urgency Levels for Hospital Blood Requests
        public static final String[] URGENCY_LEVELS = {
                "ROUTINE", "URGENT", "STAT"
        };
}