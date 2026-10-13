package com.bloodbank.bloodbankdonormanagementsystem.repository;

import com.bloodbank.bloodbankdonormanagementsystem.comparator.BloodRequestPriorityComparator;
import com.bloodbank.bloodbankdonormanagementsystem.comparator.BloodUnitExpiryComparator;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;

import java.util.*;

public class BloodBankDataStore {

    /**
     * MAP UPGRADES FOR O(1) LOOKUPS:
     * - donorMap: Lookup donor by donorId in O(1) time.
     * - inventoryMap: Lookup individual unit by barcode in O(1) time.
     * - requestMap: Lookup hospital request by requestId in O(1) time.
     */
    private final Map<String, Donor> donorMap = new HashMap<>();
    private final Map<String, BloodUnit> inventoryMap = new HashMap<>();
    private final Map<String, BloodRequest> requestMap = new HashMap<>();

    // Priority Queues for ordering
    private final PriorityQueue<BloodUnit> expiryQueue = new PriorityQueue<>(new BloodUnitExpiryComparator());
    private final PriorityQueue<BloodRequest> prioritizedRequests = new PriorityQueue<>(new BloodRequestPriorityComparator());

    // --- Donor Operations ---
    public boolean addDonor(Donor donor) {
        if (donorMap.containsKey(donor.getDonorId())) {
            return false;
        }
        donorMap.put(donor.getDonorId(), donor);
        return true;
    }

    public Optional<Donor> findDonorById(String donorId) {
        return Optional.ofNullable(donorMap.get(donorId.trim().toUpperCase()));
    }

    public Map<String, Donor> getDonorMap() {
        return Collections.unmodifiableMap(donorMap);
    }

    // --- Blood Unit Operations ---
    public void addBloodUnit(BloodUnit unit) {
        inventoryMap.put(unit.getBarcode(), unit);
        expiryQueue.offer(unit);
    }

    public Optional<BloodUnit> findUnitByBarcode(String barcode) {
        return Optional.ofNullable(inventoryMap.get(barcode.trim().toUpperCase()));
    }

    public Collection<BloodUnit> getAllUnits() {
        return Collections.unmodifiableCollection(inventoryMap.values());
    }

    public List<BloodUnit> getAvailableUnitsSortedByExpiry() {
        List<BloodUnit> available = new ArrayList<>();
        for (BloodUnit unit : inventoryMap.values()) {
            if ("AVAILABLE".equalsIgnoreCase(unit.getStatus())) {
                available.add(unit);
            }
        }
        available.sort(new BloodUnitExpiryComparator());
        return available;
    }

    // --- Request Operations ---
    public void addRequest(BloodRequest request) {
        requestMap.put(request.getRequestId(), request);
        prioritizedRequests.offer(request);
    }

    public Optional<BloodRequest> findRequestById(String requestId) {
        return Optional.ofNullable(requestMap.get(requestId.trim().toUpperCase()));
    }

    public List<BloodRequest> getRequestsSortedByPriority() {
        List<BloodRequest> requests = new ArrayList<>(requestMap.values());
        requests.sort(new BloodRequestPriorityComparator());
        return requests;
    }
}