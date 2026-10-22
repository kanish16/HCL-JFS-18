public class MonthlyUsageAnalyzer {

    // Slab constants
    static final int SLAB_1_LIMIT = 100;
    static final int SLAB_2_LIMIT = 200;

    static final double SLAB_1_RATE = 2.0;
    static final double SLAB_2_RATE = 3.0;
    static final double SLAB_3_RATE = 5.0;

    public static void main(String[] args) {

        // Monthly electricity usage for one house
        int[] monthlyUsage = {
                120, 150, 180, 220,
                250, 300, 275, 260,
                210, 190, 160, 140
        };

        System.out.println("===== MONTHLY USAGE ANALYSER =====");

        analyseHouse(monthlyUsage);

        System.out.println("\n===== 3-HOUSE ANALYSIS =====");

        // 2-D array: 3 houses x 12 months
        int[][] houseUsage = {
                {
                        120, 150, 180, 220,
                        250, 300, 275, 260,
                        210, 190, 160, 140
                },
                {
                        80, 110, 140, 170,
                        200, 230, 210, 190,
                        160, 130, 100, 90
                },
                {
                        300, 320, 350, 380,
                        400, 420, 410, 390,
                        360, 340, 310, 290
                }
        };

        for (int house = 0; house < houseUsage.length; house++) {

            System.out.println("\nHouse " + (house + 1));

            analyseHouse(houseUsage[house]);
        }
    }

    static void analyseHouse(int[] usage) {

        // long is used to prevent integer overflow
        long total = 0;

        int max = usage[0];
        int min = usage[0];

        for (int month = 0; month < usage.length; month++) {

            int currentUsage = usage[month];

            total += currentUsage;

            if (currentUsage > max) {
                max = currentUsage;
            }

            if (currentUsage < min) {
                min = currentUsage;
            }
        }

        // Explicit casting to calculate average
        double average = (double) total / usage.length;

        // Cast average back to int
        int averageUsage = (int) average;

        // Grade using ternary operator
        char grade =
                averageUsage <= 150 ? 'A' :
                averageUsage <= 250 ? 'B' :
                averageUsage <= 350 ? 'C' : 'D';

        double estimatedBill = calculateBill(usage);

        System.out.println("Total Usage      : " + total + " units");
        System.out.println("Average Usage    : " + average);
        System.out.println("Average (int)    : " + averageUsage);
        System.out.println("Maximum Usage    : " + max + " units");
        System.out.println("Minimum Usage    : " + min + " units");
        System.out.println("Usage Grade      : " + grade);
        System.out.println("Estimated Bill   : ₹" + estimatedBill);
    }

    static double calculateBill(int[] usage) {

        double totalBill = 0;

        for (int units : usage) {

            if (units <= SLAB_1_LIMIT) {

                totalBill += units * SLAB_1_RATE;

            } else if (units <= SLAB_2_LIMIT) {

                totalBill +=
                        SLAB_1_LIMIT * SLAB_1_RATE
                        + (units - SLAB_1_LIMIT) * SLAB_2_RATE;

            } else {

                totalBill +=
                        SLAB_1_LIMIT * SLAB_1_RATE
                        + (SLAB_2_LIMIT - SLAB_1_LIMIT) * SLAB_2_RATE
                        + (units - SLAB_2_LIMIT) * SLAB_3_RATE;
            }
        }

        return totalBill;
    }
}