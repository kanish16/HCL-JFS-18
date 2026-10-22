import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Deliverable: GenericToolkit.java
 * Git Commit: day-9: generics
 * Done When: No raw types or unchecked warnings
 *
 * Covers:
 * 1. Pair<K, V> immutable key-value tuple.
 * 2. Box<T extends Number> bounded type parameter calculating double value.
 * 3. Generic method <T extends Comparable<T>> max() finding the maximum element.
 * 4. Bounded wildcard methods adhering to PECS (Producer Extends, Consumer Super).
 * 5. Parsing CSV content via try-with-resources.
 * 6. Multiple custom AutoCloseable resources proving reverse-order teardown.
 */
public class GenericToolkit {

    // --- 1. Generic Pair<K, V> ---

    public static final class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = Objects.requireNonNull(key, "Key cannot be null");
            this.value = Objects.requireNonNull(value, "Value cannot be null");
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return String.format("(%s, %s)", key, value);
        }
    }

    // --- 2. Upper-Bounded Type: Box<T extends Number> ---

    public static final class Box<T extends Number> {
        private final T item;

        public Box(T item) {
            this.item = Objects.requireNonNull(item, "Item cannot be null");
        }

        public T getItem() { return item; }

        public double doubleValue() {
            return item.doubleValue();
        }

        @Override
        public String toString() {
            return "Box[" + item + "]";
        }
    }

    // --- 3. Bounded Generic Method: max() ---

    public static <T extends Comparable<T>> T max(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }
        T currentMax = list.get(0);
        for (T element : list) {
            if (element.compareTo(currentMax) > 0) {
                currentMax = element;
            }
        }
        return currentMax;
    }

    // --- 4. Wildcard Methods (PECS: Producer Extends, Consumer Super) ---

    // Producer Extends: Reads numbers from any subtype of Number and calculates sum
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }

    // Consumer Super: Writes integers into a collection capable of accepting Integer or its ancestors
    public static void appendRange(List<? super Integer> destination, int start, int end) {
        for (int i = start; i <= end; i++) {
            destination.add(i);
        }
    }

    // --- 5. Try-with-Resources CSV Reader ---

    public static final class Product {
        private final int id;
        private final String name;
        private final double price;

        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("[%d] %-12s @ $%.2f", id, name, price);
        }
    }

    public static List<Product> readProductsCsv(String csvData) {
        List<Product> products = new ArrayList<>();

        // try-with-resources: AutoCloseable BufferedReader
        try (BufferedReader reader = new BufferedReader(new StringReader(csvData))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (isHeader) {
                    isHeader = false; // Skip CSV header row
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length == 3) {
                    int id = Integer.parseInt(tokens[0].trim());
                    String name = tokens[1].trim();
                    double price = Double.parseDouble(tokens[2].trim());
                    products.add(new Product(id, name, price));
                }
            }
        } catch (IOException e) {
            System.err.println("I/O error parsing CSV: " + e.getMessage());
        }

        return products;
    }

    // --- 6. Custom AutoCloseable Showing Teardown Order ---

    public static final class TrackedResource implements AutoCloseable {
        private final String name;

        public TrackedResource(String name) {
            this.name = name;
            System.out.println("Resource OPENED: " + name);
        }

        public void operate() {
            System.out.println("Resource WORKING: " + name);
        }

        @Override
        public void close() {
            System.out.println("Resource CLOSED: " + name);
        }
    }

    public static void demonstrateCloseOrder() {
        System.out.println("=== Starting try-with-resources (LIFO close demonstration) ===");
        // Resources declared in sequence: resA first, resB second
        try (TrackedResource resA = new TrackedResource("PrimaryDatabaseConnection");
             TrackedResource resB = new TrackedResource("AuditLogStream")) {
            resA.operate();
            resB.operate();
        }
        System.out.println("=== Exited try-with-resources block ===");
    }

    // --- Main Verification Runner ---

    public static void main(String[] args) {
        System.out.println("--- 1. Pair<K, V> Demonstration ---");
        Pair<String, Integer> studentAge = new Pair<>("Jordan", 24);
        Pair<Integer, String> httpStatus = new Pair<>(404, "Not Found");
        System.out.println("Pair 1: " + studentAge.getKey() + " -> " + studentAge.getValue());
        System.out.println("Pair 2: " + httpStatus.getKey() + " -> " + httpStatus.getValue());

        System.out.println("\n--- 2. Box<T extends Number> Demonstration ---");
        Box<Integer> intBox = new Box<>(128);
        Box<Double> doubleBox = new Box<>(99.95);
        System.out.println(intBox + " doubleValue = " + intBox.doubleValue());
        System.out.println(doubleBox + " doubleValue = " + doubleBox.doubleValue());

        System.out.println("\n--- 3. Bounded Generic max() Demonstration ---");
        List<Integer> integers = List.of(15, 84, 42, 99, 3);
        List<String> words = List.of("Apple", "Zebra", "Mango", "Banana");
        System.out.println("Max Integer: " + max(integers));
        System.out.println("Max String:  " + max(words));

        System.out.println("\n--- 4. Wildcard PECS Demonstration ---");
        // <? extends Number> (Producer)
        List<Double> doubles = List.of(1.5, 2.5, 3.5);
        System.out.println("Sum of integers: " + sumOfList(integers));
        System.out.println("Sum of doubles:  " + sumOfList(doubles));

        // <? super Integer> (Consumer)
        List<Number> numberList = new ArrayList<>();
        appendRange(numberList, 1, 4);
        System.out.println("Consumer list after append: " + numberList);

        System.out.println("\n--- 5. CSV Parsing (try-with-resources) ---");
        String csvContent = """
                id,name,price
                101,Mechanical Keyboard,89.99
                102,Ergonomic Mouse,49.50
                103,4K Monitor,299.00
                """;
        List<Product> parsedProducts = readProductsCsv(csvContent);
        parsedProducts.forEach(System.out::println);

        System.out.println("\n--- 6. AutoCloseable Reverse-Order Teardown ---");
        demonstrateCloseOrder();
    }
}