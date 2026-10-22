package com.bloodbank.bloodbankdonormanagementsystem;

import com.bloodbank.bloodbankdonormanagementsystem.exception.BloodBankException;
import com.bloodbank.bloodbankdonormanagementsystem.exception.DonorIneligibleException;
import com.bloodbank.bloodbankdonormanagementsystem.exception.IncompatibleBloodUnitException;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodRequest;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.DonationCamp;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.repository.CrudRepository;
import com.bloodbank.bloodbankdonormanagementsystem.repository.InMemoryRepository;
import com.bloodbank.bloodbankdonormanagementsystem.service.BloodBankReportingService;
import com.bloodbank.bloodbankdonormanagementsystem.service.DonorNotificationService;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.util.CsvDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@SpringBootApplication
public class BloodBankDonorManagementSystemApplication implements CommandLineRunner {

	// Strictly typed generic repository instantiations (Zero Raw Types / Warnings)
	private static final CrudRepository<Donor, String> donorRepository =
			new InMemoryRepository<>(Donor::getDonorId);

	private static final CrudRepository<BloodUnit, String> bloodUnitRepository =
			new InMemoryRepository<>(BloodUnit::getBarcode);

	private static final CrudRepository<BloodRequest, String> bloodRequestRepository =
			new InMemoryRepository<>(BloodRequest::getRequestId);

	private static final CrudRepository<DonationCamp, String> donationCampRepository =
			new InMemoryRepository<>(DonationCamp::getCampId);

	private static BloodBankReportingService reportingService;

	private static Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(BloodBankDonorManagementSystemApplication.class, args);
	}

	private static DonorNotificationService notificationService;

	@Override
	public void run(String... args) {
		scanner = new Scanner(System.in);

		// --- Load CSV Seed Data via Generic Repositories ---
		System.out.println("\n--- Seeding Initial Data from CSV ---");
		CsvDataLoader.loadDonorsFromCsv("data/donors.csv", donorRepository);
		CsvDataLoader.loadBloodUnitsFromCsv("data/blood_units.csv", bloodUnitRepository);
		reportingService = new BloodBankReportingService(donorRepository, bloodUnitRepository, bloodRequestRepository);
		notificationService = new DonorNotificationService(donorRepository);
		System.out.println("Active Donors count: " + donorRepository.count());
		System.out.println("Active Inventory count: " + bloodUnitRepository.count() + "\n");

		boolean running = true;
		System.out.println("=================================================");
		System.out.println("  BLOOD BANK & DONOR MANAGEMENT SYSTEM (JFS-18)  ");
		System.out.println("=================================================");

		do {
			printMenu();
			System.out.print("Select an option (0-9): ");

			if (!scanner.hasNextInt()) {
				String invalidInput = scanner.next();
				System.out.println("\n[!] Invalid input: '" + invalidInput + "'. Please enter a number between 0 and 9.\n");
				continue;
			}

			int choice = scanner.nextInt();
			scanner.nextLine();

			try {
				switch (choice) {
					case 1 -> handleRegisterDonor();
					case 2 -> handleRecordDonation();
					case 3 -> handleComputeUnitExpiry();
					case 4 -> handleRaiseHospitalRequest();
					case 5 -> handleSuggestCompatibleUnits();
					case 6 -> handleIssueUnits();
					case 7 -> handleFlagExpiringUnits();
					case 8 -> handleViewStockDashboard();
					case 9 -> handleEmergencyBroadcast();
					case 0 -> {
						System.out.println("\nExiting Blood Bank System. Session closed.");
						notificationService.shutdown();
						running = false;
					}
					default -> System.out.println("\n[!] Choice out of range. Please choose between 0 and 8.\n");
				}
			} catch (DonorIneligibleException | IncompatibleBloodUnitException ex) {
				System.out.println("\n[BUSINESS RULE VIOLATION] " + ex.getMessage());
				System.out.println("-> Menu recovered safely.\n");
			} catch (BloodBankException ex) {
				System.out.println("\n[APPLICATION ERROR] " + ex.getMessage());
				System.out.println("-> Menu recovered safely.\n");
			} catch (IllegalArgumentException ex) {
				System.out.println("\n[VALIDATION ERROR] " + ex.getMessage());
				System.out.println("-> Menu recovered safely.\n");
			} catch (Exception ex) {
				System.out.println("\n[ERROR] " + ex.getMessage());
				System.out.println("-> Menu recovered safely.\n");
			}

		} while (running);
	}

	private static void printMenu() {
		System.out.println("-------------------------------------------------");
		System.out.println("1. [FR1] Register Donor & View Eligibility Date");
		System.out.println("2. [FR2] Record Donation & Create Blood Unit");
		System.out.println("3. [FR3] Compute Unit Expiry by Component Type");
		System.out.println("4. [FR4] Raise Hospital Blood Request");
		System.out.println("5. [FR5] Suggest Compatible Units (FEFO)");
		System.out.println("6. [FR6] Issue Units Against Request");
		System.out.println("7. [FR7] View Expiry Alerts (Expiring in 3 Days)");
		System.out.println("8. [FR8] View Stock Dashboard by Blood Group");
		System.out.println("9. [FR9] Emergency Async Donor Broadcast (CompletableFuture)");
		System.out.println("0. Exit");
		System.out.println("-------------------------------------------------");
	}

	private static void handleRegisterDonor() {
		System.out.println("\n--- [FR1] Register New Donor ---");
		System.out.print("Enter full name: ");
		String name = scanner.nextLine();
		System.out.print("Enter age (18 - 65): ");
		int age = Integer.parseInt(scanner.nextLine().trim());
		System.out.print("Enter weight in kg: ");
		double weight = Double.parseDouble(scanner.nextLine().trim());
		System.out.print("Enter blood group: ");
		String bloodGroup = scanner.nextLine().trim().toUpperCase();
		System.out.print("Enter phone number: ");
		String phone = scanner.nextLine().trim();

		Donor donor = new Donor(name, age, weight, bloodGroup, phone);
		donor.validateEligibilityForDonation();
		donorRepository.save(donor);

		System.out.println("\n[SUCCESS] Donor Registered: " + donor.getDonorId() + " (" + donor.getName() + ")\n");
	}

	private static void handleRecordDonation() {
		System.out.println("\n--- [FR2] Record Donation ---");
		System.out.print("Enter Donor ID: ");
		String donorId = scanner.nextLine().trim();

		Donor donor = donorRepository.findById(donorId)
				.orElseThrow(() -> new IllegalArgumentException("Donor ID '" + donorId + "' not found in Repository."));

		donor.validateEligibilityForDonation();

		System.out.print("Enter Component Type (WHOLE_BLOOD / PRBC / PLATELETS / FFP): ");
		String component = scanner.nextLine().trim().toUpperCase();

		BloodUnit unit = new BloodUnit(donor.getDonorId(), donor.getBloodGroup(), component);
		bloodUnitRepository.save(unit);
		donor.setLastDonationDate(LocalDate.now());

		System.out.println("\n[SUCCESS] Unit Bagged: " + unit.getBarcode() + " | Expiry: " + unit.getExpiryDate() + "\n");
	}

	private static void handleComputeUnitExpiry() {
		reportingService.generateFefoExpiryReport();
	}
	private static void handleRaiseHospitalRequest() {
		System.out.println("\n--- [FR4] Raise Hospital Blood Request ---");
		System.out.print("Enter Hospital Name: ");
		String hospital = scanner.nextLine().trim();
		System.out.print("Enter Required Blood Group (e.g., B+): ");
		String bloodGroup = scanner.nextLine().trim().toUpperCase();
		System.out.print("Enter Component Type (PRBC / FFP / PLATELETS): ");
		String component = scanner.nextLine().trim().toUpperCase();
		System.out.print("Enter Units Required: ");
		int units = Integer.parseInt(scanner.nextLine().trim());
		System.out.print("Enter Urgency Level (STAT / URGENT / ROUTINE): ");
		String urgency = scanner.nextLine().trim().toUpperCase();

		BloodRequest request = new BloodRequest(hospital, bloodGroup, component, units, urgency);
		bloodRequestRepository.save(request);

		System.out.println("\n[SUCCESS] Hospital Request Logged in Request Repository!");
		System.out.println("-> Request ID: " + request.getRequestId());
		System.out.println("-> Urgency: " + request.getUrgency() + " (Priority Triage Ranked)");
		System.out.println("-> Status: " + request.getStatus() + "\n");
	}

	private static void handleSuggestCompatibleUnits() {
		System.out.println("\n--- [FR5] Suggest Compatible Units (Lambda Strategy Pattern) ---");
		System.out.print("Enter Request ID: ");
		String reqId = scanner.nextLine().trim();

		BloodRequest req = bloodRequestRepository.findById(reqId)
				.orElseThrow(() -> new IllegalArgumentException("Request ID '" + reqId + "' not found."));

		// Strategy resolved functionally from lambda registry (No if-else ladder)
		CompatibilityStrategy strategy = CompatibilityStrategy.getStrategyForComponent(req.getComponentType());

		Set<String> compatibleGroups = strategy.getCompatibleDonorGroups(req.getBloodGroup());
		System.out.println("Target: " + req.getBloodGroup() + " " + req.getComponentType());
		System.out.println("Compatible Donor Groups: " + compatibleGroups);

		List<BloodUnit> suggestions = bloodUnitRepository.findAll().stream()
				.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
				.filter(u -> u.getComponentType().equalsIgnoreCase(req.getComponentType()))
				.filter(u -> compatibleGroups.contains(u.getBloodGroup()))
				.sorted(Comparator.comparing(BloodUnit::getExpiryDate)
						.thenComparing(BloodUnit::getCollectionDate))
				.toList();

		if (suggestions.isEmpty()) {
			System.out.println("[ALERT] No matching units available in stock.\n");
			return;
		}

		System.out.println("\nMatching Units (FEFO Ordered):");
		suggestions.forEach(u -> System.out.printf("-> Barcode: %s | Group: %s | Expiry: %s\n",
				u.getBarcode(), u.getBloodGroup(), u.getExpiryDate()));
		System.out.println();
	}

	private static void handleIssueUnits() {
		System.out.println("\n--- [FR6] Issue Units Against Hospital Request ---");
		System.out.print("Enter Request ID: ");
		String reqId = scanner.nextLine().trim();

		BloodRequest request = bloodRequestRepository.findById(reqId)
				.orElseThrow(() -> new IllegalArgumentException("Request ID '" + reqId + "' not found."));

		if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
			System.out.println("[!] Request is already " + request.getStatus() + ". Cannot issue units.\n");
			return;
		}

		System.out.print("Enter Blood Unit Barcode to issue: ");
		String barcode = scanner.nextLine().trim();

		BloodUnit unit = bloodUnitRepository.findById(barcode)
				.orElseThrow(() -> new IllegalArgumentException("Barcode '" + barcode + "' not found."));

		if (!"AVAILABLE".equalsIgnoreCase(unit.getStatus())) {
			System.out.println("[!] Unit " + unit.getBarcode() + " is currently " + unit.getStatus() + ". Cannot issue.\n");
			return;
		}

		// Verify compatibility before issuing
		CompatibilityStrategy strategy = CompatibilityStrategy.getStrategyForComponent(request.getComponentType());

		if (!strategy.getCompatibleDonorGroups(request.getBloodGroup()).contains(unit.getBloodGroup())) {
			throw new IncompatibleBloodUnitException(unit.getBloodGroup(), request.getBloodGroup());
		}

		// Update states
		unit.setStatus("ISSUED");
		request.setStatus("FULFILLED");

		System.out.println("\n[SUCCESS] Unit Dispatched Successfully!");
		System.out.println("-> Issued Unit: " + unit.getBarcode() + " (" + unit.getBloodGroup() + " " + unit.getComponentType() + ")");
		System.out.println("-> Hospital: " + request.getHospitalName());
		System.out.println("-> Request ID: " + request.getRequestId() + " status updated to FULFILLED.\n");
	}

	private static void handleFlagExpiringUnits() {
		reportingService.generateExpiryAlertReport(3);
	}

	private static void handleViewStockDashboard() {
		reportingService.generateStockDashboardReport();
		reportingService.generateDonorEligibilityReport();
	}

	/**
	 * Concept Hook: CompletableFuture parallel dispatch with safe failure handling
	 */
	private static void handleEmergencyBroadcast() {
		System.out.println("\n--- [FR9] Emergency Async Donor Broadcast ---");
		System.out.print("Enter Target Hospital Request ID: ");
		String reqId = scanner.nextLine().trim();

		BloodRequest request = bloodRequestRepository.findById(reqId)
				.orElseThrow(() -> new IllegalArgumentException("Request ID '" + reqId + "' not found."));

		System.out.printf("[BROADCAST STARTED] Initiating parallel notification dispatch for %s %s...\n",
				request.getBloodGroup(), request.getComponentType());

		long startTime = System.currentTimeMillis();

		// Trigger CompletableFuture async pipeline
		CompletableFuture<List<DonorNotificationService.NotificationResult>> future =
				notificationService.sendEmergencyAlertsAsync(request);

		// UI non-blocking indication or wait with get()/join()
		try {
			List<DonorNotificationService.NotificationResult> results = future.get(5, java.util.concurrent.TimeUnit.SECONDS);

			long elapsed = System.currentTimeMillis() - startTime;
			System.out.println("\n--- Broadcast Dispatch Summary (Completed in " + elapsed + " ms) ---");
			System.out.printf("%-10s | %-16s | %-12s | %-10s | %-30s%n",
					"DONOR ID", "NAME", "PHONE", "STATUS", "REASON/PAYLOAD");
			System.out.println("----------------------------------------------------------------------------------------");

			for (var r : results) {
				String statusLabel = r.success() ? "[DELIVERED]" : "[FAILED]";
				System.out.printf("%-10s | %-16s | %-12s | %-10s | %-30s%n",
						r.donorId(), r.donorName(), r.phone(), statusLabel, r.message());
			}

			long successful = results.stream().filter(DonorNotificationService.NotificationResult::success).count();
			System.out.printf("\nSent: %d | Delivered: %d | Fallbacks Handled: %d\n",
					results.size(), successful, (results.size() - successful));
			System.out.println("[INFO] System stayed alive and menu recovered without crash.\n");

		} catch (TimeoutException te) {
			System.err.println("[TIMEOUT] Asynchronous dispatch took too long. Cancelling remaining tasks.");
			future.cancel(true);
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("[ERROR] Async execution failed safely: " + e.getMessage());
		}
	}
}