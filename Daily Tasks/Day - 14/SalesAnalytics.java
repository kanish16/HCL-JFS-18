import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SalesAnalytics {

    // Domain models
    public record OrderItem(String productName, String category, double price, int quantity) {}

    public enum OrderStatus { DELIVERED, CANCELLED, PENDING }

    public record Order(
            String orderId,
            String customerName,
            List<OrderItem> items,
            OrderStatus status
    ) {
        public double getTotalAmount() {
            return items.stream()
                    .mapToDouble(item -> item.price() * item.quantity())
                    .sum();
        }

        public int getTotalItemCount() {
            return items.stream()
                    .mapToInt(OrderItem::quantity)
                    .sum();
        }
    }

    public static void main(String[] args) {
        List<Order> orders = generateMockOrders(1000);

        System.out.println("==================================================");
        System.out.println("              SALES ANALYTICS REPORT              ");
        System.out.println("==================================================\n");

        // 1. Revenue by Category (groupingBy + summingDouble)
        Map<String, Double> revenueByCategory = orders.stream()
                .flatMap(order -> order.items().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::category,
                        Collectors.summingDouble(item -> item.price() * item.quantity())
                ));

        System.out.println("--- 1. Revenue by Category ---");
        revenueByCategory.forEach((cat, rev) ->
                System.out.printf("• %-15s : $%,12.2f%n", cat, rev));

        // 2. Top 3 Customers by Total Spend
        List<Map.Entry<String, Double>> top3Customers = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::customerName,
                        Collectors.summingDouble(Order::getTotalAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .toList();

        System.out.println("\n--- 2. Top 3 Customers by Spend ---");
        int rank = 1;
        for (Map.Entry<String, Double> entry : top3Customers) {
            System.out.printf("%d. %-15s : $%,10.2f%n", rank++, entry.getKey(), entry.getValue());
        }

        // 3. Orders Partitioned by Status (Delivered vs Others)
        Map<Boolean, List<Order>> partitionedOrders = orders.stream()
                .collect(Collectors.partitioningBy(
                        order -> order.status() == OrderStatus.DELIVERED
                ));

        System.out.println("\n--- 3. Partitioned by Status (Delivered vs Non-Delivered) ---");
        System.out.printf("Delivered Orders Count     : %d%n", partitionedOrders.get(true).size());
        System.out.printf("Pending/Cancelled Orders   : %d%n", partitionedOrders.get(false).size());

        // 4. Average Basket Size (Items per Order)
        double averageBasketSize = orders.stream()
                .mapToInt(Order::getTotalItemCount)
                .average()
                .orElse(0.0);

        System.out.println("\n--- 4. Average Basket Size ---");
        System.out.printf("Average Items per Order    : %.2f items%n", averageBasketSize);

        // 5. Comma-Joined Distinct Product List
        String uniqueProducts = orders.stream()
                .flatMap(order -> order.items().stream())
                .map(OrderItem::productName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));

        System.out.println("\n--- 5. All Unique Products Sold ---");
        System.out.println(uniqueProducts);

        // 6. Sequential vs. Parallel Timing for CPU-Heavy Map Operation
        runBenchmark(orders);
    }

    private static void runBenchmark(List<Order> orders) {
        System.out.println("\n==================================================");
        System.out.println("     BENCHMARK: SEQUENTIAL vs PARALLEL STREAMS    ");
        System.out.println("==================================================");

        // Sequential execution
        long startSeq = System.nanoTime();
        List<Double> seqResults = orders.stream()
                .map(SalesAnalytics::simulateCpuHeavyOperation)
                .toList();
        long endSeq = System.nanoTime();
        double seqDurationMs = (endSeq - startSeq) / 1_000_000.0;

        // Parallel execution
        long startPar = System.nanoTime();
        List<Double> parResults = orders.parallelStream()
                .map(SalesAnalytics::simulateCpuHeavyOperation)
                .toList();
        long endPar = System.nanoTime();
        double parDurationMs = (endPar - startPar) / 1_000_000.0;

        System.out.printf("Sequential Execution Time : %8.2f ms (Processed: %d)%n", seqDurationMs, seqResults.size());
        System.out.printf("Parallel Execution Time   : %8.2f ms (Processed: %d)%n", parDurationMs, parResults.size());
        System.out.printf("Performance Speedup       : %.2fx%n", (seqDurationMs / parDurationMs));
    }

    // Simulates an intensive calculation (e.g., trigonometric transformations)
    private static double simulateCpuHeavyOperation(Order order) {
        double checksum = order.getTotalAmount();
        for (int i = 0; i < 50_000; i++) {
            checksum = Math.sin(checksum) * Math.cos(checksum) + Math.sqrt(Math.abs(checksum) + 1.0);
        }
        return checksum;
    }

    // Helper method to generate deterministic mock data
    private static List<Order> generateMockOrders(int count) {
        String[] customers = {"Alice Smith", "Bob Jones", "Charlie Brown", "Diana Prince", "Evan Wright", "Fiona Gallagher"};
        List<OrderItem> catalog = List.of(
                new OrderItem("MacBook Pro", "Electronics", 1999.99, 1),
                new OrderItem("Wireless Mouse", "Electronics", 29.99, 2),
                new OrderItem("Clean Code", "Books", 35.50, 1),
                new OrderItem("Desk Lamp", "Home & Office", 45.00, 1),
                new OrderItem("Coffee Beans", "Groceries", 18.25, 3),
                new OrderItem("Mechanical Keyboard", "Electronics", 120.00, 1),
                new OrderItem("Ergonomic Chair", "Furniture", 280.00, 1)
        );

        Random random = new Random(42);
        OrderStatus[] statuses = OrderStatus.values();

        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> {
                    String customer = customers[random.nextInt(customers.length)];
                    OrderStatus status = statuses[random.nextInt(statuses.length)];
                    int itemCount = 1 + random.nextInt(4);
                    List<OrderItem> items = IntStream.range(0, itemCount)
                            .mapToObj(j -> catalog.get(random.nextInt(catalog.size())))
                            .toList();
                    return new Order("ORD-" + i, customer, items, status);
                })
                .toList();
    }
}