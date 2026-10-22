import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class BloodBankApplication {
    static Scanner sc = new Scanner(System.in);
    static ArrayList<Donor> donors = new ArrayList<>();
    static ArrayList<BloodUnit> inventory = new ArrayList<>();

    enum BloodGroup {
        O_NEGATIVE, O_POSITIVE, A_NEGATIVE, A_POSITIVE,
        B_NEGATIVE, B_POSITIVE, AB_NEGATIVE, AB_POSITIVE
    }

    enum UnitStatus { AVAILABLE, ISSUED, EXPIRED }

    static class Donor {
        private int id;
        private String name;
        private int age;
        private BloodGroup bloodGroup;

        Donor(int id, String name, int age, BloodGroup bloodGroup) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.bloodGroup = bloodGroup;
        }

        boolean isEligible() {
            // Basic classroom age check only; medical screening is still required.
            return age >= 18 && age <= 65;
        }

        public String toString() {
            return "ID: " + id + ", Name: " + name + ", Age: " + age
                    + ", Blood Group: " + bloodGroup
                    + ", Basic age eligibility: " + (isEligible() ? "Eligible" : "Not eligible");
        }
    }

    static class BloodUnit {
        private String unitId;
        private BloodGroup bloodGroup;
        private LocalDate expiryDate;
        private UnitStatus status;

        BloodUnit(String unitId, BloodGroup bloodGroup, LocalDate expiryDate) {
            this.unitId = unitId;
            this.bloodGroup = bloodGroup;
            this.expiryDate = expiryDate;
            this.status = expiryDate.isBefore(LocalDate.now())
                    ? UnitStatus.EXPIRED : UnitStatus.AVAILABLE;
        }

        public String toString() {
            return "Unit ID: " + unitId + ", Group: " + bloodGroup
                    + ", Expiry: " + expiryDate + ", Status: " + status;
        }
    }

    public static void main(String[] args) {
        loadSampleInventory();

        while (true) {
            System.out.println("\n=== BLOOD BANK SYSTEM ===");
            System.out.println("1. Donor Registration & Eligibility");
            System.out.println("2. Hospital Blood Request & Issue");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> registerDonor();
                case "2" -> hospitalRequest();
                case "0" -> {
                    System.out.println("Application closed.");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid option. Choose 1, 2, or 0.");
            }
        }
    }

    // CORE FLOW 1: Donor registration and basic eligibility
    static void registerDonor() {
        System.out.println("\n--- Donor Registration ---");
        try {
            System.out.print("Enter donor ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            if (id <= 0) {
                System.out.println("Donor ID must be positive.");
                return;
            }

            for (Donor donor : donors) {
                if (donor.id == id) {
                    System.out.println("This donor ID already exists.");
                    return;
                }
            }

            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
                return;
            }

            System.out.print("Enter age: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            if (age <= 0) {
                System.out.println("Age must be positive.");
                return;
            }

            System.out.print("Enter blood group (e.g. O_POSITIVE): ");
            BloodGroup group = BloodGroup.valueOf(sc.nextLine().trim().toUpperCase());

            Donor donor = new Donor(id, name, age, group);
            donors.add(donor);

            System.out.println("\nRegistration successful!");
            System.out.println(donor);
            if (donor.isEligible()) {
                System.out.println("Basic age check passed. Medical screening is still required.");
            } else {
                System.out.println("Donor fails the basic age rule (18-65).");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID and age must be whole numbers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid blood group. Use O_NEGATIVE, O_POSITIVE, A_NEGATIVE, "
                    + "A_POSITIVE, B_NEGATIVE, B_POSITIVE, AB_NEGATIVE, or AB_POSITIVE.");
        }
    }

    // CORE FLOW 2: Request compatible units and issue after confirmation
    static void hospitalRequest() {
        System.out.println("\n--- Hospital Blood Request ---");
        System.out.print("Enter hospital name: ");
        String hospital = sc.nextLine().trim();
        if (hospital.isEmpty()) {
            System.out.println("Hospital name cannot be empty.");
            return;
        }

        try {
            System.out.print("Enter required blood group (e.g. O_POSITIVE): ");
            BloodGroup requestedGroup = BloodGroup.valueOf(sc.nextLine().trim().toUpperCase());

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(sc.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                return;
            }

            ArrayList<BloodUnit> matches = new ArrayList<>();
            for (BloodUnit unit : inventory) {
                if (unit.status == UnitStatus.AVAILABLE
                        && compatible(requestedGroup, unit.bloodGroup)) {
                    matches.add(unit);
                }
            }

            matches.sort(Comparator.comparing(unit -> unit.expiryDate));

            System.out.println("\nHospital: " + hospital);
            System.out.println("Compatible available units: " + matches.size());

            if (matches.size() < quantity) {
                System.out.println("Insufficient compatible stock. No units were issued.");
                return;
            }

            System.out.println("Selected units (earliest expiry first):");
            for (int i = 0; i < quantity; i++) {
                System.out.println(matches.get(i));
            }

            System.out.print("Confirm issue? (yes/no): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("yes")) {
                System.out.println("Request cancelled. No units issued.");
                return;
            }

            for (int i = 0; i < quantity; i++) {
                matches.get(i).status = UnitStatus.ISSUED;
            }

            System.out.println("Request fulfilled for " + hospital + ".");
            System.out.println("Issued unit IDs:");
            for (int i = 0; i < quantity; i++) {
                System.out.println(matches.get(i).unitId);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input. Check the blood group and quantity.");
        }
    }

    // Simplified red-cell compatibility rules for a classroom exercise.
    static boolean compatible(BloodGroup recipient, BloodGroup donor) {
        return switch (recipient) {
            case O_NEGATIVE -> donor == BloodGroup.O_NEGATIVE;
            case O_POSITIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.O_POSITIVE;
            case A_NEGATIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.A_NEGATIVE;
            case A_POSITIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.O_POSITIVE
                    || donor == BloodGroup.A_NEGATIVE
                    || donor == BloodGroup.A_POSITIVE;
            case B_NEGATIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.B_NEGATIVE;
            case B_POSITIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.O_POSITIVE
                    || donor == BloodGroup.B_NEGATIVE
                    || donor == BloodGroup.B_POSITIVE;
            case AB_NEGATIVE -> donor == BloodGroup.O_NEGATIVE
                    || donor == BloodGroup.A_NEGATIVE
                    || donor == BloodGroup.B_NEGATIVE
                    || donor == BloodGroup.AB_NEGATIVE;
            case AB_POSITIVE -> true;
        };
    }

    static void loadSampleInventory() {
        inventory.add(new BloodUnit("BU101", BloodGroup.O_NEGATIVE, LocalDate.now().plusDays(2)));
        inventory.add(new BloodUnit("BU102", BloodGroup.O_POSITIVE, LocalDate.now().plusDays(7)));
        inventory.add(new BloodUnit("BU103", BloodGroup.A_POSITIVE, LocalDate.now().plusDays(5)));
        inventory.add(new BloodUnit("BU104", BloodGroup.B_POSITIVE, LocalDate.now().plusDays(10)));
    }
}
