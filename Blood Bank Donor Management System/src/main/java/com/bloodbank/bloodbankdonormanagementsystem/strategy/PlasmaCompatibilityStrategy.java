package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlasmaCompatibilityStrategy implements CompatibilityStrategy {

    private static final Map<String, Set<String>> PLASMA_MAP = Map.of(
            "O+",  Set.of("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"),
            "O-",  Set.of("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"),
            "A+",  Set.of("A+", "A-", "AB+", "AB-"),
            "A-",  Set.of("A+", "A-", "AB+", "AB-"),
            "B+",  Set.of("B+", "B-", "AB+", "AB-"),
            "B-",  Set.of("B+", "B-", "AB+", "AB-"),
            "AB+", Set.of("AB+", "AB-"),
            "AB-", Set.of("AB+", "AB-")
    );

    @Override
    public Set<String> getCompatibleDonorGroups(String recipientGroup) {
        return PLASMA_MAP.getOrDefault(recipientGroup.toUpperCase(), Collections.emptySet());
    }

    @Override
    public String getComponentScope() {
        return "Fresh Frozen Plasma (FFP)";
    }
}