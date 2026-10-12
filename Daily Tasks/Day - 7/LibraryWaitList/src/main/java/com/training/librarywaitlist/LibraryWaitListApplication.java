package com.training.librarywaitlist;

import com.training.librarywaitlist.benchmark.ListPerformanceBenchmark;
import com.training.librarywaitlist.model.LoanRecord;
import com.training.librarywaitlist.model.MemberType;
import com.training.librarywaitlist.service.LibraryCatalogueService;
import com.training.librarywaitlist.service.LoanManagerService;
import com.training.librarywaitlist.service.WaitlistService;

import java.util.ArrayList;
import java.util.List;

public class LibraryWaitListApplication {

    public static void main(String[] args) {
        System.out.println("=== 1. Catalogue & De-duplication ===");
        LibraryCatalogueService catalogueService = new LibraryCatalogueService();
        catalogueService.addBook("Clean Code");
        catalogueService.addBook("Effective Java");
        catalogueService.addBook("Design Patterns");
        catalogueService.addBook("Clean Code"); // Duplicate entry

        System.out.println("ArrayList count: " + catalogueService.getRawCatalogue().size());
        System.out.println("LinkedHashSet unique: " + catalogueService.getDeduplicatedCatalogue());

        System.out.println("\n=== 2. Standard FIFO Waitlist (ArrayDeque) ===");
        WaitlistService waitlistService = new WaitlistService();
        waitlistService.enqueueStandard("Alice");
        waitlistService.enqueueStandard("Bob");
        System.out.println("Queue: " + waitlistService.getStandardWaitlist());
        System.out.println("Served standard: " + waitlistService.serveNextStandard());

        System.out.println("\n=== 3. Priority Waitlist (PriorityQueue) ===");
        waitlistService.enqueuePriority("Student-John", MemberType.STUDENT);
        waitlistService.enqueuePriority("Prof-Alan", MemberType.FACULTY);
        waitlistService.enqueuePriority("Student-Mary", MemberType.STUDENT);

        while (waitlistService.hasPriorityPending()) {
            System.out.println("Served priority: " + waitlistService.serveNextPriority());
        }

        System.out.println("\n=== 4. Overdue Purge (Iterator.remove) ===");
        List<LoanRecord> loans = new ArrayList<>();
        loans.add(new LoanRecord("Java Concurrency in Practice", 5));
        loans.add(new LoanRecord("Spring Microservices", 22)); // Overdue
        loans.add(new LoanRecord("Refactoring", 35));           // Overdue

        System.out.println("Before purge: " + loans);
        new LoanManagerService().purgeOverdueLoans(loans, 14);
        System.out.println("After purge (>14 days): " + loans);

        System.out.println("\n=== 5. ArrayList vs LinkedList Benchmark ===");
        ListPerformanceBenchmark.execute(50_000, 5_000);
    }
}