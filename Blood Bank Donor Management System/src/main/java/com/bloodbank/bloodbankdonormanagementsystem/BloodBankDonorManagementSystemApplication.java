package com.bloodbank.bloodbankdonormanagementsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.CompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.RedBloodCellCompatibilityStrategy;
import com.bloodbank.bloodbankdonormanagementsystem.strategy.PlasmaCompatibilityStrategy;
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

			// Prevents crashes when invalid text is entered (DoD requirement)
			if (!scanner.hasNextInt()) {
				String invalidInput = scanner.next();
				System.out.println("\n[!] Invalid input: '" + invalidInput + "'. Please enter a number between 0 and 8.\n");
				continue;
			}

			int choice = scanner.nextInt();
			scanner.nextLine(); // Clear buffer

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
		System.out.println("\n-> [FR1] Register Donor stub invoked.\n");
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

		// Variable typed as the INTERFACE (polymorphic dependency)
		CompatibilityStrategy strategy;

		String recipientGroup = "B+";
		System.out.println("Target Recipient Blood Group: " + recipientGroup);

		// 1. RBC Strategy execution
		strategy = new RedBloodCellCompatibilityStrategy();
		System.out.println("Scope: " + strategy.getComponentScope());
		System.out.println("Compatible Donor Groups: " + strategy.getCompatibleDonorGroups(recipientGroup));

		// 2. Plasma Strategy execution (swapping implementation polymorphically)
		strategy = new PlasmaCompatibilityStrategy();
		System.out.println("Scope: " + strategy.getComponentScope());
		System.out.println("Compatible Donor Groups: " + strategy.getCompatibleDonorGroups(recipientGroup) + "\n");
	}

	private static void handleIssueUnits() {
		System.out.println("\n-> [FR6] Issue Units stub invoked.\n");
	}

	private static void handleFlagExpiringUnits() {
		System.out.println("\n-> [FR7] Expiry Alert stub invoked.\n");
	}

	private static void handleViewStockDashboard() {
		System.out.println("\n-> [FR8] Stock Dashboard stub invoked.\n");
	}
}