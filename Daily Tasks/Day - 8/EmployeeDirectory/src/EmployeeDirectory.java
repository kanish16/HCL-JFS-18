import java.util.*;

/**
 * Deliverable: EmployeeDirectory.java
 * Git Commit: day-8: collections-framework
 *
 * Covers:
 * 1. HashMap O(1) key lookup.
 * 2. Department frequency counting using Map.merge().
 * 3. Sorted department roll-up using TreeMap.
 * 4. Natural ordering with Comparable (by ID).
 * 5. Multi-level Comparator chaining for Top-5 earners (Salary DESC, then Name ASC).
 * 6. Demonstration and fix of the "missing hashCode()" contract bug.
 */
public class EmployeeDirectory {

    // --- Domain Models ---

    /**
     * Correctly implements Comparable, equals, and hashCode.
     */
    public static class Employee implements Comparable<Employee> {
        private final int id;
        private final String name;
        private final String department;
        private final double salary;

        public Employee(int id, String name, String department, double salary) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public double getSalary() { return salary; }

        @Override
        public int compareTo(Employee other) {
            return Integer.compare(this.id, other.id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee)) return false;
            Employee employee = (Employee) o;
            return id == employee.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }

        @Override
        public String toString() {
            return String.format("[%d] %-12s | Dept: %-11s | Salary: $%,10.2f", id, name, department, salary);
        }
    }

    /**
     * Buggy Key Class: Implements equals() but Omits hashCode().
     * Violates Java's Object contract.
     */
    public static class BrokenEmployeeKey {
        private final int id;

        public BrokenEmployeeKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BrokenEmployeeKey)) return false;
            BrokenEmployeeKey that = (BrokenEmployeeKey) o;
            return id == that.id;
        }

        // Intentionally omitted hashCode(): inherits Object.hashCode() based on identity/memory address.
    }

    /**
     * Fixed Key Class: Properly overrides both equals() and hashCode().
     */
    public static class FixedEmployeeKey {
        private final int id;

        public FixedEmployeeKey(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FixedEmployeeKey)) return false;
            FixedEmployeeKey that = (FixedEmployeeKey) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    // --- Directory Implementation ---

    private final Map<Integer, Employee> employeeById = new HashMap<>();
    private final List<Employee> allEmployees = new ArrayList<>();

    public void addEmployee(Employee emp) {
        employeeById.put(emp.getId(), emp);
        allEmployees.add(emp);
    }

    // 1. Fast Lookup by ID
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(employeeById.get(id));
    }

    // 2. Frequency Count using Map.merge()
    public Map<String, Integer> getDepartmentHeadcounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Employee emp : allEmployees) {
            counts.merge(emp.getDepartment(), 1, Integer::sum);
        }
        return counts;
    }

    // 3. Department Roll-up sorted naturally by department name
    public Map<String, List<Employee>> getDepartmentReport() {
        Map<String, List<Employee>> treeMap = new TreeMap<>();
        for (Employee emp : allEmployees) {
            treeMap.computeIfAbsent(emp.getDepartment(), k -> new ArrayList<>()).add(emp);
        }
        return treeMap;
    }

    // 4. Natural ordering demonstration (Comparable by ID)
    public List<Employee> getEmployeesSortedById() {
        List<Employee> sortedList = new ArrayList<>(allEmployees);
        Collections.sort(sortedList);
        return sortedList;
    }

    // 5. Top-5 Earners using Comparator Chaining: Salary DESC, then Name ASC
    public List<Employee> getTopEarners(int limit) {
        Comparator<Employee> topEarnerComparator = Comparator
                .comparingDouble(Employee::getSalary).reversed()
                .thenComparing(Employee::getName);

        return allEmployees.stream()
                .sorted(topEarnerComparator)
                .limit(limit)
                .toList();
    }

    // 6. Missing hashCode Bug Demonstration and Resolution
    public static void demonstrateHashCodeContract() {
        System.out.println("=== 6. Equals/HashCode Contract Demo ===");

        // Failure Case: BrokenEmployeeKey
        Map<BrokenEmployeeKey, String> brokenMap = new HashMap<>();
        BrokenEmployeeKey key1 = new BrokenEmployeeKey(101);
        brokenMap.put(key1, "Alice Smith");

        BrokenEmployeeKey lookupKeyBroken = new BrokenEmployeeKey(101);
        System.out.println("Key equals match: " + key1.equals(lookupKeyBroken));
        System.out.println("Broken lookup result (expected null): " + brokenMap.get(lookupKeyBroken));

        // Fixed Case: FixedEmployeeKey
        Map<FixedEmployeeKey, String> fixedMap = new HashMap<>();
        FixedEmployeeKey key2 = new FixedEmployeeKey(101);
        fixedMap.put(key2, "Alice Smith");

        FixedEmployeeKey lookupKeyFixed = new FixedEmployeeKey(101);
        System.out.println("Fixed lookup result: " + fixedMap.get(lookupKeyFixed));
    }

    // --- Main Runner ---

    public static void main(String[] args) {
        EmployeeDirectory directory = new EmployeeDirectory();

        // Sample Data Seed
        List<Employee> sampleData = List.of(
                new Employee(108, "Jordan Lee", "Engineering", 145000),
                new Employee(102, "Sarah Connor", "Security", 125000),
                new Employee(105, "Alex Murphy", "Security", 130000),
                new Employee(101, "Alan Turing", "Engineering", 195000),
                new Employee(109, "Ada Lovelace", "Engineering", 195000),
                new Employee(103, "Grace Hopper", "Operations", 160000),
                new Employee(107, "Claude Shannon", "Operations", 140000),
                new Employee(104, "Linus Torvalds", "Engineering", 175000)
        );

        sampleData.forEach(directory::addEmployee);

        // 1. HashMap Lookup
        System.out.println("=== 1. HashMap Lookup by ID (104) ===");
        directory.findById(104).ifPresent(System.out::println);

        // 2. Frequency Count with Map.merge()
        System.out.println("\n=== 2. Department Headcounts (Map.merge) ===");
        directory.getDepartmentHeadcounts().forEach((dept, count) ->
                System.out.printf("%-12s: %d employees%n", dept, count));

        // 3. TreeMap Department Report
        System.out.println("\n=== 3. Sorted Department Report (TreeMap) ===");
        directory.getDepartmentReport().forEach((dept, list) -> {
            System.out.println("[" + dept + "]");
            list.forEach(emp -> System.out.println("  " + emp));
        });

        // 4. Comparable Natural Order (ID ASC)
        System.out.println("\n=== 4. Natural Order (Comparable: ID ASC) ===");
        directory.getEmployeesSortedById().forEach(System.out::println);

        // 5. Top-5 Salary Comparator Chain
        System.out.println("\n=== 5. Top-5 Earners (Salary DESC, then Name ASC) ===");
        directory.getTopEarners(5).forEach(System.out::println);

        // 6. Bug Demo
        System.out.println();
        demonstrateHashCodeContract();
    }
}