package com.bloodbank.bloodbankdonormanagementsystem.service;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.repository.CrudRepository;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class DonorNotificationService {

    // Dedicated thread pool for async I/O simulation
    private final ExecutorService notificationExecutor = Executors.newFixedThreadPool(4);
    private final CrudRepository<Donor, String> donorRepo;

    public DonorNotificationService(CrudRepository<Donor, String> donorRepo) {
        this.donorRepo = donorRepo;
    }

    public record NotificationResult(String donorId, String donorName, String phone, boolean success, String message) {}

    /**
     * Non-blocking Bulk Donor Dispatch using CompletableFuture
     * Done when: All futures complete and failures are caught without crashing.
     */
    public CompletableFuture<List<NotificationResult>> sendEmergencyAlertsAsync(BloodRequest request) {
        CompatibilityStrategy strategy = CompatibilityStrategy.getStrategyForComponent(request.getComponentType());
        Set<String> compatibleGroups = strategy.getCompatibleDonorGroups(request.getBloodGroup());

        // 1. Identify candidate donors eligible for broadcast
        List<Donor> eligibleDonors = donorRepo.findAll().stream()
                .filter(Donor::isEligibleToDonate)
                .filter(d -> compatibleGroups.contains(d.getBloodGroup()))
                .toList();

        if (eligibleDonors.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        // 2. Spawn a CompletableFuture pipeline for each donor in parallel
        List<CompletableFuture<NotificationResult>> futures = eligibleDonors.stream()
                .map(donor -> CompletableFuture.supplyAsync(() -> simulateSmsDispatch(donor, request), notificationExecutor)
                        // SAFE RECOVERY: Catch runtime timeouts or carrier exceptions without crashing
                        .exceptionally(ex -> {
                            System.err.println("[ASYNC FALLBACK] Failed sending alert to " + donor.getName() + ": " + ex.getMessage());
                            return new NotificationResult(
                                    donor.getDonorId(),
                                    donor.getName(),
                                    donor.getPhone(),
                                    false,
                                    "FAILED: " + ex.getMessage()
                            );
                        }))
                .toList();

        // 3. Compose all futures with CompletableFuture.allOf
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }

    /**
     * Simulated network / SMS gateway call with random transient failure
     */
    private NotificationResult simulateSmsDispatch(Donor donor, BloodRequest request) {
        try {
            // Simulate 400-800ms carrier latency
            Thread.sleep(400 + (long) (Math.random() * 400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Notification thread interrupted", e);
        }

        // Simulate a 25% random external SMS gateway failure to test failure handling
        if (donor.getPhone().endsWith("0") || Math.random() < 0.25) {
            throw new RuntimeException("SMS Gateway 503: Carrier timed out for " + donor.getPhone());
        }

        return new NotificationResult(
                donor.getDonorId(),
                donor.getName(),
                donor.getPhone(),
                true,
                "SMS Delivered (Need " + request.getBloodGroup() + " at " + request.getHospitalName() + ")"
        );
    }

    public void shutdown() {
        notificationExecutor.shutdown();
    }
}