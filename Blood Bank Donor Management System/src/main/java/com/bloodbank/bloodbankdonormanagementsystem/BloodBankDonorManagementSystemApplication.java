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
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.util.CsvDataLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

	private static Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(BloodBankDonorManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) {
		scanner = new Scanner(System.in);

		// --- Load CSV Seed Data via Generic Repositories ---
		System.out.println("\n--- Seeding Initial Data from CSV ---");
		CsvDataLoader.loadDonorsFromCsv("data/donors.csv", donorRepository);
		CsvDataLoader.loadBloodUnitsFromCsv("data/blood_units.csv", bloodUnitRepository);
		System.out.println("Active Donors count: " + donorRepository.count());
		System.out.println("Active Inventory count: " + bloodUnitRepository.count() + "\n");

		boolean running = true;
		System.out.println("=================================================");
		System.out.println("  BLOOD BANK & DONOR MANAGEMENT SYSTEM (JFS-18)  ");
		System.out.println("=================================================");

		do {
			printMenu();
			System.out.print("Select an option (0-8): ");

			if (!scanner.hasNextInt()) {
				String invalidInput = scanner.next();
				System.out.println("\n[!] Invalid input: '" + invalidInput + "'. Please enter a number between 0 and 8.\n");
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
					case 0 -> {
						System.out.println("\nExiting Blood Bank System. Session closed.");
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
		System.out.println("\n--- [FR3] Sorted Inventory Expiry Report (FEFO Comparator) ---");
		List<BloodUnit> units = bloodUnitRepository.findAll().stream()
				.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
				.sorted(Comparator.comparing(BloodUnit::getExpiryDate)
						.thenComparing(BloodUnit::getCollectionDate))
				.toList();

		if (units.isEmpty()) {
			System.out.println("[INFO] No available units in inventory.\n");
			return;
		}

		System.out.printf("%-10s | %-6s | %-12s | %-12s | %-12s | %-10s%n",
				"BARCODE", "GROUP", "COMPONENT", "COLLECTED", "EXPIRY DATE", "STATUS");
		System.out.println("-----------------------------------------------------------------------");
		for (BloodUnit u : units) {
			System.out.printf("%-10s | %-6s | %-12s | %-12s | %-12s | %-10s%n",
					u.getBarcode(), u.getBloodGroup(), u.getComponentType(),
					u.getCollectionDate(), u.getExpiryDate(), u.getStatus());
		}
		System.out.println();
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
		System.out.println("\n--- [FR7] Expiry Alert Tracker (Next 3 Days) ---");
		LocalDate today = LocalDate.now();
		LocalDate alertThreshold = today.plusDays(3);

		List<BloodUnit> expiringUnits = bloodUnitRepository.findAll().stream()
				.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
				.filter(u -> !u.getExpiryDate().isBefore(today) && !u.getExpiryDate().isAfter(alertThreshold))
				.sorted(Comparator.comparing(BloodUnit::getExpiryDate))
				.toList();

		if (expiringUnits.isEmpty()) {
			System.out.println("[INFO] No units are nearing expiration within the next 3 days.\n");
			return;
		}

		System.out.println("[CRITICAL ALERT] The following units expire soon and must be prioritized (FEFO):");
		System.out.printf("%-10s | %-6s | %-12s | %-12s | %-15s%n",
				"BARCODE", "GROUP", "COMPONENT", "EXPIRY", "DAYS REMAINING");
		System.out.println("------------------------------------------------------------------");

		for (BloodUnit unit : expiringUnits) {
			long daysLeft = ChronoUnit.DAYS.between(today, unit.getExpiryDate());
			System.out.printf("%-10s | %-6s | %-12s | %-12s | %d day(s)%n",
					unit.getBarcode(), unit.getBloodGroup(), unit.getComponentType(),
					unit.getExpiryDate(), daysLeft);
		}
		System.out.println();
	}

	private static void handleViewStockDashboard() {
		System.out.println("\n--- [FR8] Blood Stock Dashboard Summary ---");
		List<BloodUnit> allUnits = bloodUnitRepository.findAll();

		if (allUnits.isEmpty()) {
			System.out.println("[INFO] Stock is completely empty.\n");
			return;
		}

		// Aggregate counts by blood group for AVAILABLE units
		Map<String, Long> stockByGroup = allUnits.stream()
				.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
				.collect(Collectors.groupingBy(BloodUnit::getBloodGroup, Collectors.counting()));

		// Aggregate counts by component type
		Map<String, Long> stockByComponent = allUnits.stream()
				.filter(u -> "AVAILABLE".equalsIgnoreCase(u.getStatus()))
				.collect(Collectors.groupingBy(BloodUnit::getComponentType, Collectors.counting()));

		long totalAvailable = stockByGroup.values().stream().mapToLong(Long::longValue).sum();

		System.out.println("AVAILABLE UNITS BY BLOOD GROUP:");
		System.out.printf("%-12s | %-8s%n", "BLOOD GROUP", "UNITS");
		System.out.println("-------------------------");
		String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
		for (String bg : groups) {
			System.out.printf("%-12s | %-8d%n", bg, stockByGroup.getOrDefault(bg, 0L));
		}

		System.out.println("\nAVAILABLE UNITS BY COMPONENT TYPE:");
		stockByComponent.forEach((comp, count) -> System.out.printf("%-12s : %d unit(s)%n", comp, count));

		System.out.println("-------------------------");
		System.out.println("Total Available Stock: " + totalAvailable + " unit(s)");
		System.out.println("Total Registered Donors: " + donorRepository.count() + "\n");
	}
}