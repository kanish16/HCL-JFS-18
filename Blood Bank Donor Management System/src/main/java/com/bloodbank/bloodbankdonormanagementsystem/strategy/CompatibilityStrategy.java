package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface CompatibilityStrategy {

    Set<String> getCompatibleDonorGroups(String recipientBloodGroup);

    Map<String, Set<String>> RBC_MATRIX = Map.of(
            "O-", Set.of("O-"),
            "O+", Set.of("O-", "O+"),
            "A-", Set.of("O-", "A-"),
            "A+", Set.of("O-", "O+", "A-", "A+"),
            "B-", Set.of("O-", "B-"),
            "B+", Set.of("O-", "O+", "B-", "B+"),
            "AB-", Set.of("O-", "A-", "B-", "AB-"),
            "AB+", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
    );

    Map<String, Set<String>> PLASMA_MATRIX = Map.of(
            "O-", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),
            "O+", Set.of("O+", "A+", "B+", "AB+"),
            "A-", Set.of("A-", "A+", "AB-", "AB+"),
            "A+", Set.of("A+", "AB+"),
            "B-", Set.of("B-", "B+", "AB-", "AB+"),
            "B+", Set.of("B+", "AB+"),
            "AB-", Set.of("AB-", "AB+"),
            "AB+", Set.of("AB+")
    );

    CompatibilityStrategy RED_BLOOD_CELL = recipient ->
            RBC_MATRIX.getOrDefault(recipient, Collections.emptySet());

    CompatibilityStrategy PLASMA = recipient ->
            PLASMA_MATRIX.getOrDefault(recipient, Collections.emptySet());

    CompatibilityStrategy PLATELETS = recipient ->
            RBC_MATRIX.getOrDefault(recipient, Collections.emptySet());

    Map<String, CompatibilityStrategy> STRATEGY_REGISTRY = Map.of(
            "PRBC", RED_BLOOD_CELL,
            "WHOLE_BLOOD", RED_BLOOD_CELL,
            "FFP", PLASMA,
            "PLATELETS", PLATELETS
    );

    static CompatibilityStrategy getStrategyForComponent(String componentType) {
        if (componentType == null) {
            return RED_BLOOD_CELL;
        }
        return STRATEGY_REGISTRY.getOrDefault(componentType.toUpperCase(), RED_BLOOD_CELL);
    }
}