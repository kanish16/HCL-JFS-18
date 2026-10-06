package com.bloodbank.bloodbankdonormanagementsystem;

public final class SampleBloodData {

    private SampleBloodData() {}

    // 7 days of collection data (Day 1 through Day 7)
    public static final String[] SAMPLE_DAYS = {
            "Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7"
    };

    // Daily whole blood units collected across the 7 days
    public static final int[] DAILY_UNITS_COLLECTED = {
            14, 18, 11, 22, 19, 25, 16
    };

    // 2-D Array: Units collected per day for 8 blood groups [Day][BloodGroup]
    // Order: [A+, A-, B+, B-, AB+, AB-, O+, O-]
    public static final int[][] WEEKLY_STOCK_MATRIX = {
            { 3, 1, 4, 1, 1, 0, 3, 1 }, // Day 1
            { 4, 1, 5, 2, 2, 0, 3, 1 }, // Day 2
            { 2, 0, 3, 1, 1, 1, 2, 1 }, // Day 3
            { 5, 2, 6, 2, 2, 1, 3, 1 }, // Day 4
            { 4, 1, 4, 2, 2, 1, 4, 1 }, // Day 5
            { 6, 2, 7, 3, 3, 0, 3, 1 }, // Day 6
            { 3, 1, 4, 1, 2, 1, 3, 1 }  // Day 7
    };

    // Sample donor registrations for the week [donorName, bloodGroup, age, weightKg]
    public static final String[][] SAMPLE_DONORS = {
            { "John Doe", "O+", "28", "68.5" },
            { "Alice Smith", "A-", "34", "55.0" },
            { "Robert Ray", "B+", "22", "72.0" },
            { "Emma Watson", "AB+", "45", "61.0" },
            { "David Miller", "O-", "31", "80.2" }
    };
    //sample
}