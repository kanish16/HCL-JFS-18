package com.bloodbank.bloodbankdonormanagementsystem.strategy;

import java.util.Set;

public interface CompatibilityStrategy {
    Set<String> getCompatibleDonorGroups(String recipientGroup);
    String getComponentScope();
}