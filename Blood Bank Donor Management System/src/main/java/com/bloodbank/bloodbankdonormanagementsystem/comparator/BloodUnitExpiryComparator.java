package com.bloodbank.bloodbankdonormanagementsystem.comparator;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import java.util.Comparator;

public class BloodUnitExpiryComparator implements Comparator<BloodUnit> {
    @Override
    public int compare(BloodUnit u1, BloodUnit u2) {
        int expiryComparison = u1.getExpiryDate().compareTo(u2.getExpiryDate());
        if (expiryComparison != 0) {
            return expiryComparison;
        }
        return u1.getCollectionDate().compareTo(u2.getCollectionDate());
    }
}