package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RedBloodCellCompatibilityStrategy implements CompatibilityStrategy {

    private static final Map<String, Set<String>> COMPATIBILITY_MAP = Map.of(
            "O-", Set.of("O-"),
            "O+", Set.of("O-", "O+"),
            "A-", Set.of("O-", "A-"),
            "A+", Set.of("O-", "O+", "A-", "A+"),
            "B-", Set.of("O-", "B-"),
            "B+", Set.of("O-", "O+", "B-", "B+"),
            "AB-", Set.of("O-", "A-", "B-", "AB-"),
            "AB+", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
    );

    @Override
    public Set<String> getCompatibleDonorGroups(String recipientBloodGroup) {
        if (recipientBloodGroup == null) {
            return Collections.emptySet();
        }
        return COMPATIBILITY_MAP.getOrDefault(recipientBloodGroup.toUpperCase(), Collections.emptySet());
    }
}