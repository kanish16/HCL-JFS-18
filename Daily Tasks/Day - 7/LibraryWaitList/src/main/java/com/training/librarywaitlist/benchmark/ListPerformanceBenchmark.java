package com.training.librarywaitlist.benchmark;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListPerformanceBenchmark {

    public static void execute(int insertElements, int getOperations) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        // 1. Head Insertions (Index 0)
        long start = System.nanoTime();
        for (int i = 0; i < insertElements; i++) {
            arrayList.add(0, i);
        }
        long alInsertNs = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < insertElements; i++) {
            linkedList.add(0, i);
        }
        long llInsertNs = System.nanoTime() - start;

        // 2. Random Access get(i)
        start = System.nanoTime();
        for (int i = 0; i < getOperations; i++) {
            arrayList.get(i);
        }
        long alGetNs = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < getOperations; i++) {
            linkedList.get(i);
        }
        long llGetNs = System.nanoTime() - start;

        System.out.println("=========================================================================================");
        System.out.printf("%-26s | %-12s | %-20s | %-16s%n", "Operation", "Collection", "Complexity", "Time (ms)");
        System.out.println("=========================================================================================");
        System.out.printf("%-26s | %-12s | %-20s | %10.2f ms%n", "Head Insert (" + insertElements + ")", "ArrayList", "O(N) copy shifts", alInsertNs / 1_000_000.0);
        System.out.printf("%-26s | %-12s | %-20s | %10.2f ms%n", "Head Insert (" + insertElements + ")", "LinkedList", "O(1) pointer relink", llInsertNs / 1_000_000.0);
        System.out.printf("%-26s | %-12s | %-20s | %10.2f ms%n", "Random get(i) (" + getOperations + ")", "ArrayList", "O(1) direct address", alGetNs / 1_000_000.0);
        System.out.printf("%-26s | %-12s | %-20s | %10.2f ms%n", "Random get(i) (" + getOperations + ")", "LinkedList", "O(N) traversal", llGetNs / 1_000_000.0);
        System.out.println("=========================================================================================");
    }
}