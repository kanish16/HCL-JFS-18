import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FilterEngine {
    static class Product {
        private final String name;
        private final String category;
        private final double price;
        private final boolean inStock;

        Product(String name, String category, double price, boolean inStock) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.inStock = inStock;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public boolean isInStock() { return inStock; }

        @Override
        public String toString() {
            return String.format("%-15s | %-12s | Rs. %-8.2f | In stock: %s",
                    name, category, price, inStock);
        }
    }

    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Keyboard", "Electronics", 1800, true));
        products.add(new Product("Mouse", "Electronics", 700, true));
        products.add(new Product("Monitor", "Electronics", 9500, false));
        products.add(new Product("Notebook", "Stationery", 120, true));
        products.add(new Product("Pen Set", "Stationery", 250, false));
        products.add(new Product("Backpack", "Accessories", 1400, true));
        products.add(new Product("Headphones", "Electronics", 2200, true));

        // Supplier creates a default product.
        Supplier<Product> defaultProduct =
                () -> new Product("USB Cable", "Electronics", 300, true);
        products.add(defaultProduct.get());

        // Predicates define and compose the filtering rules.
        Predicate<Product> withinBudget = product -> product.getPrice() <= 2000;
        Predicate<Product> electronics =
                product -> product.getCategory().equalsIgnoreCase("Electronics");
        Predicate<Product> inStock = Product::isInStock;

        Predicate<Product> filter = withinBudget.and(electronics).and(inStock);

        // Function chain: Product -> name -> uppercase name.
        Function<Product, String> getName = Product::getName;
        Function<String, String> uppercase = String::toUpperCase;
        Function<Product, String> nameInUppercase = getName.andThen(uppercase);

        // Consumer prints each product.
        Consumer<Product> printProduct = System.out::println;

        System.out.println("=== All Products ===");
        products.forEach(printProduct);

        System.out.println("\n=== Matching Products (Electronics, <= Rs. 2000, In Stock) ===");
        List<Product> filteredProducts = products.stream()
                .filter(filter)
                .toList();
        filteredProducts.forEach(printProduct);

        System.out.println("\n=== Function Chain: Uppercase Product Names ===");
        filteredProducts.stream()
                .map(nameInUppercase)
                .forEach(System.out::println);

        System.out.println("\n=== Supplier Default Product ===");
        System.out.println(defaultProduct.get());
    }
}
