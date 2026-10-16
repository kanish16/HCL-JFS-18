import java.util.Scanner;
public class StudentServiceTest {
    static int StuID;
    public static void main(String[] args) {
        //int StuID;
        StudentService service = new StudentService();
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== StudentService Testing =====");

        // Test 1: Add a valid student
        try {
                    System.out.print("Enter Student ID: ");
                    StuID = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    System.out.print("Enter Student Name: ");
                    String StuName = scanner.nextLine();
                    System.out.print("Enter Student Email: ");
                    String StuEmail = scanner.nextLine();
                    Student student = new Student(StuID, StuName, StuEmail);
                    service.addStudent(student);

            System.out.println("Test 1: PASS - Valid student added");
        } catch (Exception e) {
            System.out.println("Test 1: FAIL - " + e.getMessage());
        }

        // Test 2: Add a null student
        try {
            service.addStudent(null);

            System.out.println("Test 2: FAIL - Null student was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("Test 2: PASS - Null student rejected");
        }

        // Test 3: Add a duplicate student ID
        try {
            service.addStudent(
                    new Student(101, "Bala", "bala@gmail.com")
            );

            System.out.println("Test 3: FAIL - Duplicate ID accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("Test 3: PASS - Duplicate ID rejected");
        }

        // Test 4: Update with null
        try {
            service.updateStudent(null);

            System.out.println("Test 4: FAIL - Null update accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("Test 4: PASS - Null update rejected");
        }

        // Test 5: Update a non-existing student
        boolean updated = service.updateStudent(
                new Student(999, "Kumar", "kumar@gmail.com")
        );

        if (!updated) {
            System.out.println("Test 5: PASS - Missing student not updated");
        } else {
            System.out.println("Test 5: FAIL - Missing student updated");
        }

        // Test 6: Delete an existing student
        boolean deleted = service.deleteStudent(StuID);

        if (deleted) {
            System.out.println("Test 6: PASS - Student deleted");
        } else {
            System.out.println("Test 6: FAIL - Student was not deleted");
        }

        System.out.println("===== Testing Completed =====");
    }
}
