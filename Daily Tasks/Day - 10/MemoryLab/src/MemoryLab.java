import java.util.ArrayList;
import java.util.List;

/**
 * Deliverable: MemoryLab.java
 * Git Commit: day-10: jvm-memory-internals
 *
 * Covers:
 * 1. StackOverflowError recursion depth measurement (tunable via -Xss).
 * 2. OutOfMemoryError heap exhaustion demo (tunable via -Xmx64m).
 * 3. Performance benchmarking: String concatenation (+=) vs StringBuilder.
 * 4. Triggering explicit allocations to observe Garbage Collection (-Xlog:gc).
 * 5. Integer cache boundary pitfall (identity == vs value equality .equals()).
 */
public class MemoryLab {

    // --- 1. Stack Depth Measurement ---

    private static int recursionDepth = 0;

    public static void measureStackDepth() {
        recursionDepth = 0;
        try {
            recursiveCall();
        } catch (StackOverflowError e) {
            System.out.printf("StackOverflowError encountered at call depth: %,d%n", recursionDepth);
        }
    }

    private static void recursiveCall() {
        recursionDepth++;
        recursiveCall();
    }

    // --- 2. Heap OutOfMemoryError Demonstration ---

    public static void triggerHeapOom() {
        System.out.println("Allocating memory to induce OutOfMemoryError...");
        List<byte[]> memoryHog = new ArrayList<>();
        try {
            while (true) {
                memoryHog.add(new byte[1024 * 1024]); // 1 MB
            }
        } catch (OutOfMemoryError e) {
            memoryHog.clear(); // Release references so JVM can recover and finish main cleanly
            System.out.println("OutOfMemoryError successfully caught! Memory cleared.");
        }
    }
    // --- 3. String Concatenation vs. StringBuilder Benchmark ---

    public static void benchmarkStringPerformance(int iterations) {
        // String += concatenation (O(N^2) memory reallocation)
        long startStr = System.nanoTime();
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str += "a";
        }
        long durationStr = System.nanoTime() - startStr;

        // StringBuilder append (O(N) amortized dynamic array)
        long startSb = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("a");
        }
        long durationSb = System.nanoTime() - startSb;

        System.out.printf("String (+-) time for %,d iterations: %,.2f ms%n",
                iterations, durationStr / 1_000_000.0);
        System.out.printf("StringBuilder time for %,d iterations: %,.2f ms%n",
                iterations, durationSb / 1_000_000.0);
        System.out.printf("Speedup factor: %,.1fx faster%n", (double) durationStr / Math.max(durationSb, 1));
    }

    // --- 4. Integer Cache Demonstration ---

    public static void demonstrateIntegerCache() {
        // Cached range by default: -128 to 127
        Integer a1 = 127;
        Integer a2 = 127;

        Integer b1 = 128;
        Integer b2 = 128;

        System.out.println("Testing cached value (127):");
        System.out.println("  127 == 127: " + (a1 == a2) + " (Both point to cached IntegerCache object)");
        System.out.println("  127.equals(127): " + a1.equals(a2));

        System.out.println("Testing value outside cache (128):");
        System.out.println("  128 == 128: " + (b1 == b2) + " (Different heap instances due to autoboxing)");
        System.out.println("  128.equals(128): " + b1.equals(b2));
    }

    // --- Main Driver ---

    public static void main(String[] args) {
        System.out.println("=== 1. StackOverflow Depth Test ===");
        measureStackDepth();

        System.out.println("\n=== 2. Integer Cache Contract Demo ===");
        demonstrateIntegerCache();

        System.out.println("\n=== 3. String += vs StringBuilder Performance ===");
        benchmarkStringPerformance(50_000);

        System.out.println("\n=== 4. OutOfMemoryError Simulation ===");
        triggerHeapOom();
    }
}