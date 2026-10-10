package com.bloodbank.bloodbankdonormanagementsystem;

import com.bloodbank.bloodbankdonormanagementsystem.exception.BloodBankException;
import com.bloodbank.bloodbankdonormanagementsystem.exception.DonorIneligibleException;
import com.bloodbank.bloodbankdonormanagementsystem.exception.IncompatibleBloodUnitException;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.RedBloodCellCompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.PlasmaCompatibilityStrategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Scanner;

@SpringBootApplication
public class BloodBankDonorManagementSystemApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BloodBankDonorManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);
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
					System.out.println("-> Cooldown active. Next eligible date: " + ex.getNextEligibleDate());
				}
				System.out.println("-> Menu state recovered.\n");
			} catch (IncompatibleBloodUnitException ex) {
				System.out.println("\n[BUSINESS RULE VIOLATION - COMPATIBILITY]");
				System.out.println("-> " + ex.getMessage());
				System.out.println("-> Action aborted. Menu state recovered.\n");
			} catch (BloodBankException ex) {
				System.out.println("\n[APPLICATION ERROR] " + ex.getMessage());
				System.out.println("-> Menu state recovered.\n");
			} catch (Exception ex) {
				System.out.println("\n[UNEXPECTED ERROR] System caught an unhandled failure: " + ex.getMessage());
				System.out.println("-> Menu state recovered.\n");
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
		System.out.println("\n-> [FR1] Testing Donor Eligibility Rule...");
		// Simulating donor registration within cooldown window (last donation 20 days ago)
		Donor recentDonor = new Donor("Arun Kumar", 24, 62.0, "O+", "9876543210", LocalDate.now().minusDays(20));
		recentDonor.validateEligibilityForDonation();
	}

	private static void handleRecordDonation() {
		System.out.println("\n-> [FR2] Record Donation stub invoked.\n");
	}

	private static void handleComputeUnitExpiry() {
		System.out.println("\n-> [FR3] Compute Expiry stub invoked.\n");
	}

	private static void handleRaiseHospitalRequest() {
		System.out.println("\n-> [FR4] Raise Request stub invoked.\n");
	}

	private static void handleSuggestCompatibleUnits() {
		System.out.println("\n-> [FR5] Suggest Compatible Units (Strategy Pattern)");
		CompatibilityStrategy strategy = new RedBloodCellCompatibilityStrategy();
		String recipientGroup = "B+";
		System.out.println("Target Recipient: " + recipientGroup);
		System.out.println("Compatible Groups: " + strategy.getCompatibleDonorGroups(recipientGroup) + "\n");
	}

	private static void handleIssueUnits() {
		System.out.println("\n-> [FR6] Testing Transfusion Compatibility Rule...");
		CompatibilityStrategy rbcStrategy = new RedBloodCellCompatibilityStrategy();
		String requestedRecipientGroup = "O+";
		String offeredDonorGroup = "AB+";

		System.out.println("Attempting to issue " + offeredDonorGroup + " unit to " + requestedRecipientGroup + " recipient...");
		if (!rbcStrategy.getCompatibleDonorGroups(requestedRecipientGroup).contains(offeredDonorGroup)) {
			throw new IncompatibleBloodUnitException(offeredDonorGroup, requestedRecipientGroup);
		}
	}

	private static void handleFlagExpiringUnits() {
		System.out.println("\n-> [FR7] Expiry Alert stub invoked.\n");
	}

	private static void handleViewStockDashboard() {
		System.out.println("\n-> [FR8] Stock Dashboard stub invoked.\n");
	}
}