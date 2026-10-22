package com.bloodbank.bloodbankdonormanagementsystem.service;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.repository.CrudRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BloodBankReportingService {

    private final CrudRepository<Donor, String> donorRepo;
    private final CrudRepository<BloodUnit, String> unitRepo;
    private final CrudRepository<BloodRequest, String> requestRepo;

    public BloodBankReportingService(CrudRepository<Donor, String> donorRepo,
                                     CrudRepository<BloodUnit, String> unitRepo,
                                     CrudRepository<BloodRequest, String> requestRepo) {
        this.donorRepo = donorRepo;
        this.unitRepo = unitRepo;
        this.requestRepo = requestRepo;
    }

    /**
     * Report 1: FEFO Inventory Expiry Report
     * Streams Pipeline: filter -> sorted (Comparator) -> collect / forEach
     */
    public void generateFefoExpiryReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                   FEFO INVENTORY EXPIRY AUDIT REPORT                     ");
        System.out.println("==========================================================================");

        List<BloodUnit> activeUnits = unitRepo.findAll().stream()
                .filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
                .sorted(Comparator.comparing(BloodUnit::getExpiryDate)
                        .thenComparing(BloodUnit::getCollectionDate))
                .toList();

        if (activeUnits.isEmpty()) {
            System.out.println("[INFO] No available units in inventory.");
            return;
        }

        System.out.printf("%-10s | %-6s | %-12s | %-12s | %-12s | %-10s%n",
                "BARCODE", "GROUP", "COMPONENT", "COLLECTED", "EXPIRY", "STATUS");
        System.out.println("--------------------------------------------------------------------------");

        activeUnits.stream()
                .map(u -> String.format("%-10s | %-6s | %-12s | %-12s | %-12s | %-10s",
                        u.getBarcode(), u.getBloodGroup(), u.getComponentType(),
                        u.getCollectionDate(), u.getExpiryDate(), u.getStatus()))
                .forEach(System.out::println);
        System.out.println();
    }

    /**
     * Report 2: Critical Expiry Alerts (Next 3 Days)
     * Streams Pipeline: filter (date range) -> sorted -> map -> forEach
     */
    public void generateExpiryAlertReport(int daysThreshold) {
        System.out.println("\n==========================================================================");
        System.out.printf("          CRITICAL EXPIRY ALERT REPORT (NEXT %d DAYS)                     \n", daysThreshold);
        System.out.println("==========================================================================");

        LocalDate today = LocalDate.now();
        LocalDate alertLimit = today.plusDays(daysThreshold);

        List<BloodUnit> expiring = unitRepo.findAll().stream()
                .filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
                .filter(u -> !u.getExpiryDate().isBefore(today) && !u.getExpiryDate().isAfter(alertLimit))
                .sorted(Comparator.comparing(BloodUnit::getExpiryDate))
                .toList();

        if (expiring.isEmpty()) {
            System.out.printf("[INFO] Zero units expiring within the next %d days.\n", daysThreshold);
            return;
        }

        System.out.printf("%-10s | %-6s | %-12s | %-12s | %-15s%n",
                "BARCODE", "GROUP", "COMPONENT", "EXPIRY DATE", "DAYS REMAINING");
        System.out.println("--------------------------------------------------------------------------");

        expiring.stream()
                .map(u -> {
                    long daysRemaining = ChronoUnit.DAYS.between(today, u.getExpiryDate());
                    return String.format("%-10s | %-6s | %-12s | %-12s | %d day(s)",
                            u.getBarcode(), u.getBloodGroup(), u.getComponentType(),
                            u.getExpiryDate(), daysRemaining);
                })
                .forEach(System.out::println);
        System.out.println();
    }

    /**
     * Report 3: Comprehensive Multi-Dimensional Stock Dashboard
     * Streams Pipeline: filter -> groupingBy (counting) + groupingBy (component)
     */
    public void generateStockDashboardReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                   BLOOD INVENTORY METRICS & DASHBOARD                    ");
        System.out.println("==========================================================================");

        List<BloodUnit> availableUnits = unitRepo.findAll().stream()
                .filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
                .toList();

        if (availableUnits.isEmpty()) {
            System.out.println("[INFO] Stock inventory is completely exhausted.");
            return;
        }

        // 1. Grouping by Blood Group
        Map<String, Long> countByGroup = availableUnits.stream()
                .collect(Collectors.groupingBy(BloodUnit::getBloodGroup, Collectors.counting()));

        // 2. Grouping by Component Type
        Map<String, Long> countByComponent = availableUnits.stream()
                .collect(Collectors.groupingBy(BloodUnit::getComponentType, Collectors.counting()));

        System.out.println("AVAILABLE STOCK BY BLOOD GROUP:");
        System.out.printf("%-12s | %-10s%n", "BLOOD GROUP", "COUNT");
        System.out.println("-------------------------");
        List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").stream()
                .map(bg -> String.format("%-12s | %-10d", bg, countByGroup.getOrDefault(bg, 0L)))
                .forEach(System.out::println);

        System.out.println("\nAVAILABLE STOCK BY COMPONENT:");
        System.out.printf("%-14s | %-10s%n", "COMPONENT", "COUNT");
        System.out.println("---------------------------");
        countByComponent.entrySet().stream()
                .map(e -> String.format("%-14s | %-10d", e.getKey(), e.getValue()))
                .forEach(System.out::println);

        long totalUnits = availableUnits.size();
        System.out.println("---------------------------");
        System.out.println("Total Active Inventory Units : " + totalUnits);
        System.out.println("Total Donors in Registry     : " + donorRepo.count());
        System.out.println();
    }

    /**
     * Report 4: Hospital Request Priority & Triage Report
     * Streams Pipeline: sorted (custom lambda comparator) -> collect
     */
    public void generateHospitalRequestTriageReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                  HOSPITAL REQUEST TRIAGE STATUS REPORT                   ");
        System.out.println("==========================================================================");

        Map<String, Integer> urgencyRanks = Map.of("STAT", 1, "URGENT", 2, "ROUTINE", 3);

        List<BloodRequest> pendingRequests = requestRepo.findAll().stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .sorted(Comparator.comparingInt((BloodRequest r) -> urgencyRanks.getOrDefault(r.getUrgency().toUpperCase(), 4))
                        .thenComparing(BloodRequest::getRequestedAt))
                .toList();

        if (pendingRequests.isEmpty()) {
            System.out.println("[INFO] No pending hospital requests.");
            return;
        }

        System.out.printf("%-10s | %-16s | %-6s | %-10s | %-6s | %-10s%n",
                "REQ ID", "HOSPITAL", "GROUP", "COMPONENT", "UNITS", "URGENCY");
        System.out.println("--------------------------------------------------------------------------");

        pendingRequests.stream()
                .map(r -> String.format("%-10s | %-16s | %-6s | %-10s | %-6d | %-10s",
                        r.getRequestId(), r.getHospitalName(), r.getBloodGroup(),
                        r.getComponentType(), r.getUnitsRequired(), r.getUrgency()))
                .forEach(System.out::println);
        System.out.println();
    }

    /**
     * Report 5: Donor Demographics & Eligibility Analytics
     * Streams Pipeline: partitioningBy -> summarizingDouble / counting
     */
    public void generateDonorEligibilityReport() {
        System.out.println("\n==========================================================================");
        System.out.println("                DONOR ELIGIBILITY & DEMOGRAPHICS SUMMARY                  ");
        System.out.println("==========================================================================");

        List<Donor> donors = donorRepo.findAll();
        if (donors.isEmpty()) {
            System.out.println("[INFO] No donors registered.");
            return;
        }

        // Partitioning into Eligible vs Ineligible (Cooling Period)
        Map<Boolean, List<Donor>> partitioned = donors.stream()
                .collect(Collectors.partitioningBy(Donor::isEligibleToDonate));

        long eligibleCount = partitioned.getOrDefault(true, Collections.emptyList()).size();
        long coolingCount = partitioned.getOrDefault(false, Collections.emptyList()).size();

        // Statistical summary using double stream
        DoubleSummaryStatistics ageStats = donors.stream()
                .collect(Collectors.summarizingDouble(Donor::getAge));

        System.out.println("Eligible to Donate Immediately : " + eligibleCount);
        System.out.println("Currently in Cooldown Window    : " + coolingCount);
        System.out.printf("Donor Average Age               : %.1f years (Min: %.0f, Max: %.0f)\n",
                ageStats.getAverage(), ageStats.getMin(), ageStats.getMax());
        System.out.println("--------------------------------------------------------------------------\n");
    }
}