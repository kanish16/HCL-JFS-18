package com.bloodbank.bloodbankdonormanagementsystem;

import com.bloodbank.bloodbankdonormanagementsystem.exception.DonorIneligibleException;
import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.repository.BloodBankDataStore;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.PlasmaCompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.RedBloodCellCompatibilityStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Scanner;

@SpringBootApplication
public class BloodBankDonorManagementSystemApplication implements CommandLineRunner {

	private static final BloodBankDataStore dataStore = new BloodBankDataStore();
	private static Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(BloodBankDonorManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) {
		scanner = new Scanner(System.in);
		boolean running = true;

		System.out.println("\n=================================================");
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
			scanner.nextLine(); // Clear buffer

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
			} catch (DonorIneligibleException ex) {
				System.out.println("\n[BUSINESS RULE VIOLATION - ELIGIBILITY]");
				System.out.println("-> " + ex.getMessage());
				if (ex.getNextEligibleDate() != null) {
					System.out.println("-> Next eligible donation date: " + ex.getNextEligibleDate());
				}
				System.out.println("-> Menu recovered safely.\n");
			} catch (IllegalArgumentException ex) {
				System.out.println("\n[VALIDATION ERROR] " + ex.getMessage());
				System.out.println("-> Menu recovered safely.\n");
			} catch (Exception ex) {
				System.out.println("\n[ERROR] Operation failed: " + ex.getMessage());
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

	/**
	 * FR1: Register Donor & View Eligibility Date
	 * Stored in Set<Donor> (no duplicates)
	 */
	private static void handleRegisterDonor() {
		System.out.println("\n--- [FR1] Register New Donor ---");
		System.out.print("Enter full name: ");
		String name = scanner.nextLine();

		System.out.print("Enter age (18 - 65): ");
		int age = Integer.parseInt(scanner.nextLine().trim());

		System.out.print("Enter weight in kg (min 50.0): ");
		double weight = Double.parseDouble(scanner.nextLine().trim());

		System.out.print("Enter blood group (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
		String bloodGroup = scanner.nextLine().trim().toUpperCase();

		System.out.print("Enter phone number: ");
		String phone = scanner.nextLine().trim();

		System.out.print("Has donor donated before? (y/n): ");
		String donatedBefore = scanner.nextLine().trim();

		LocalDate lastDonationDate = null;
		if (donatedBefore.equalsIgnoreCase("y")) {
			System.out.print("How many days ago was the last donation? ");
			int daysAgo = Integer.parseInt(scanner.nextLine().trim());
			lastDonationDate = LocalDate.now().minusDays(daysAgo);
		}

		Donor donor = new Donor(name, age, weight, bloodGroup, phone, lastDonationDate);
		donor.validateEligibilityForDonation();

		boolean added = dataStore.addDonor(donor);
		if (added) {
			System.out.println("\n[SUCCESS] Donor Registered Successfully!");
			System.out.println("-> Donor ID: " + donor.getDonorId());
			System.out.println("-> Eligibility Status: " + (donor.isEligibleToDonate() ? "ELIGIBLE NOW" : "IN COOLING PERIOD"));
			System.out.println("-> Next Eligible Date: " + donor.getNextEligibleDate() + "\n");
		} else {
			System.out.println("\n[!] Duplicate Donor: A donor with identical details already exists in the Registry.\n");
		}
	}

	/**
	 * FR2: Record Donation & Create Blood Unit
	 * Stored in List<BloodUnit> and PriorityQueue<BloodUnit>
	 */
	private static void handleRecordDonation() {
		System.out.println("\n--- [FR2] Record Donation & Bag Unit ---");
		System.out.print("Enter Donor ID (e.g., DNR-1001): ");
		String donorId = scanner.nextLine().trim();

		Optional<Donor> donorOpt = dataStore.findDonorById(donorId);
		if (donorOpt.isEmpty()) {
			System.out.println("[!] Donor not found. Please register donor first via Option 1.\n");
			return;
		}

		Donor donor = donorOpt.get();
		// Check eligibility rule before drawing donation
		donor.validateEligibilityForDonation();

		System.out.print("Enter Component Type (WHOLE_BLOOD / PRBC / PLATELETS / FFP): ");
		String component = scanner.nextLine().trim().toUpperCase();

		BloodUnit unit = new BloodUnit(donor.getDonorId(), donor.getBloodGroup(), component);
		dataStore.addBloodUnit(unit);

		// Update donor's last donation date to today
		donor.setLastDonationDate(LocalDate.now());

		System.out.println("\n[SUCCESS] Donation Logged and Bagged!");
		System.out.println("-> Barcode: " + unit.getBarcode());
		System.out.println("-> Blood Group: " + unit.getBloodGroup());
		System.out.println("-> Component: " + unit.getComponentType());
		System.out.println("-> Collection Date: " + unit.getCollectionDate());
		System.out.println("-> Expiry Date: " + unit.getExpiryDate());
		System.out.println("-> Status: " + unit.getStatus() + "\n");
	}

	/**
	 * FR3: Compute Unit Expiry by Component Type
	 * Displays all active units ordered by earliest expiry (FEFO using PriorityQueue)
	 */
	private static void handleComputeUnitExpiry() {
		System.out.println("\n--- [FR3] Unit Expiry & Shelf-Life Tracker (FEFO Order) ---");
		PriorityQueue<BloodUnit> queue = dataStore.getExpiryQueue();

		if (queue.isEmpty()) {
			System.out.println("[INFO] Inventory is currently empty. Record a donation first.\n");
			return;
		}

		System.out.printf("%-10s | %-12s | %-10s | %-12s | %-12s | %-10s%n",
				"BARCODE", "GROUP", "TYPE", "COLLECTED", "EXPIRY", "STATUS");
		System.out.println("--------------------------------------------------------------------------");

		// Polling from priority queue prints in exact FEFO (nearest expiry first) order
		while (!queue.isEmpty()) {
			BloodUnit u = queue.poll();
			System.out.printf("%-10s | %-12s | %-10s | %-12s | %-12s | %-10s%n",
					u.getBarcode(), u.getBloodGroup(), u.getComponentType(),
					u.getCollectionDate(), u.getExpiryDate(), u.getStatus());
		}
		System.out.println();
	}

	private static void handleRaiseHospitalRequest() {
		System.out.println("\n-> [FR4] Raise Hospital Blood Request stub invoked.\n");
	}

	private static void handleSuggestCompatibleUnits() {
		System.out.println("\n-> [FR5] Suggest Compatible Units (Strategy Pattern)");
		CompatibilityStrategy rbcStrategy = new RedBloodCellCompatibilityStrategy();
		CompatibilityStrategy plasmaStrategy = new PlasmaCompatibilityStrategy();
		String group = "B+";
		System.out.println("Target: " + group);
		System.out.println("Compatible RBC Donors: " + rbcStrategy.getCompatibleDonorGroups(group));
		System.out.println("Compatible Plasma Donors: " + plasmaStrategy.getCompatibleDonorGroups(group) + "\n");
	}

	private static void handleIssueUnits() {
		System.out.println("\n-> [FR6] Issue Units Against Request stub invoked.\n");
	}

	private static void handleFlagExpiringUnits() {
		System.out.println("\n-> [FR7] Expiry Alert stub invoked.\n");
	}

	private static void handleViewStockDashboard() {
		System.out.println("\n-> [FR8] Stock Dashboard stub invoked.\n");
	}
}