package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RedBloodCellCompatibilityStrategy implements CompatibilityStrategy {

    private static final Map<String, Set<String>> RBC_MAP = Map.of(
            "AB+", Set.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"),
            "AB-", Set.of("A-", "B-", "AB-", "O-"),
            "A+",  Set.of("A+", "A-", "O+", "O-"),
            "A-",  Set.of("A-", "O-"),
            "B+",  Set.of("B+", "B-", "O+", "O-"),
            "B-",  Set.of("B-", "O-"),
            "O+",  Set.of("O+", "O-"),
            "O-",  Set.of("O-")
    );

    @Override
    public Set<String> getCompatibleDonorGroups(String recipientGroup) {
        return RBC_MAP.getOrDefault(recipientGroup.toUpperCase(), Collections.emptySet());
    }

    @Override
    public String getComponentScope() {
        return "Red Blood Cells / Whole Blood (RBC)";
    }
}