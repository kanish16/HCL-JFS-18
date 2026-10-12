package com.training.librarywaitlist.service;

import com.training.librarywaitlist.model.LoanRecord;

import java.util.Iterator;
import java.util.List;

public class LoanManagerService {

    /*
     * JUSTIFICATION: Iterator.remove()
     * 1. Prevents ConcurrentModificationException: Modifying an ArrayList directly inside an enhanced
     *    for-loop changes the collection's `modCount`. The loop iterator detects the discrepancy with
     *    its internal `expectedModCount` and immediately aborts.
     * 2. Safe in-place mutation: it.remove() deletes the active element while synchronizing internal cursors.
     */
    public void purgeOverdueLoans(List<LoanRecord> loans, int maxAllowedDays) {
        Iterator<LoanRecord> iterator = loans.iterator();
        while (iterator.hasNext()) {
            LoanRecord record = iterator.next();
            if (record.getDaysOverdue() > maxAllowedDays) {
                iterator.remove();
            }
        }
    }
}