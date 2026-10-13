package com.bloodbank.bloodbankdonormanagementsystem.comparator;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import java.util.Comparator;

public class BloodRequestPriorityComparator implements Comparator<BloodRequest> {

    private int getPriorityWeight(String urgency) {
        return switch (urgency.toUpperCase()) {
            case "STAT" -> 1;
            case "URGENT" -> 2;
            case "ROUTINE" -> 3;
            default -> 4;
        };
    }

    @Override
    public int compare(BloodRequest r1, BloodRequest r2) {
        int weightCompare = Integer.compare(getPriorityWeight(r1.getUrgency()), getPriorityWeight(r2.getUrgency()));
        if (weightCompare != 0) {
            return weightCompare;
        }
        return r1.getRequestedAt().compareTo(r2.getRequestedAt());
    }
}