package com.bloodbank.bloodbankdonormanagementsystem.repository;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;

import java.util.*;

public class BloodBankDataStore {

    /**
     * COLLECTION CHOICE JUSTIFICATION:
     * 1. Set<Donor> (LinkedHashSet):
     *    - A donor must be unique based on donorId/phone (equals & hashCode guarantee no duplicates).
     *    - LinkedHashSet provides O(1) duplicate prevention while preserving registration order.
     */
    private final Set<Donor> donorRegistry = new LinkedHashSet<>();

    /**
     * COLLECTION CHOICE JUSTIFICATION:
     * 2. List<BloodUnit> (ArrayList):
     *    - Blood units represent indexed physical inventory bags collected chronologically.
     *    - ArrayList provides fast O(1) index lookups, efficient iteration, and dynamic appending.
     */
    private final List<BloodUnit> inventory = new ArrayList<>();

    /**
     * COLLECTION CHOICE JUSTIFICATION:
     * 3. PriorityQueue<BloodUnit> (FEFO / Expiry Queue):
     *    - Critical for FR3 & FR5: First-Expiring, First-Out (FEFO) dispensing policy.
     *    - Min-Heap ordered by BloodUnit::getExpiryDate delivers O(1) retrieval of the nearest-to-expire unit.
     */
    private final PriorityQueue<BloodUnit> expiryQueue = new PriorityQueue<>(
            Comparator.comparing(BloodUnit::getExpiryDate)
    );

    /**
     * COLLECTION CHOICE JUSTIFICATION:
     * 4. Queue<BloodRequest> (LinkedList / FIFO):
     *    - Hospital blood requests arrive in real time and are processed in arrival order (FIFO).
     */
    private final Queue<BloodRequest> hospitalRequestQueue = new LinkedList<>();

    // --- Accessor and Mutation Methods ---

    public boolean addDonor(Donor donor) {
        return donorRegistry.add(donor);
    }

    public Optional<Donor> findDonorById(String donorId) {
        return donorRegistry.stream()
                .filter(d -> d.getDonorId().equalsIgnoreCase(donorId.trim()))
                .findFirst();
    }

    public Set<Donor> getAllDonors() {
        return Collections.unmodifiableSet(donorRegistry);
    }

    public void addBloodUnit(BloodUnit unit) {
        inventory.add(unit);
        expiryQueue.offer(unit);
    }

    public List<BloodUnit> getAllInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public PriorityQueue<BloodUnit> getExpiryQueue() {
        return new PriorityQueue<>(expiryQueue); // defensive copy
    }

    public void addRequest(BloodRequest request) {
        hospitalRequestQueue.offer(request);
    }

    public Queue<BloodRequest> getHospitalRequestQueue() {
        return hospitalRequestQueue;
    }
}