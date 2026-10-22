package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlasmaCompatibilityStrategy implements CompatibilityStrategy {

    private static final Map<String, Set<String>> COMPATIBILITY_MAP = Map.of(
            "O-", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
            "O+", Set.of("O+", "A+", "B+", "AB+"),
            "A-", Set.of("A-", "A+", "AB-", "AB+"),
            "A+", Set.of("A+", "AB+"),
            "B-", Set.of("B-", "B+", "AB-", "AB+"),
            "B+", Set.of("B+", "AB+"),
            "AB-", Set.of("AB-", "AB+"),
            "AB+", Set.of("AB+")
    );

    @Override
    public Set<String> getCompatibleDonorGroups(String recipientBloodGroup) {
        if (recipientBloodGroup == null) {
            return Collections.emptySet();
        }
        return COMPATIBILITY_MAP.getOrDefault(recipientBloodGroup.toUpperCase(), Collections.emptySet());
    }
}