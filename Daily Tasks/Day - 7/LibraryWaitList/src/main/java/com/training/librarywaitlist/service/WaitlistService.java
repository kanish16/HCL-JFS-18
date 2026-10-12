package com.training.librarywaitlist.service;

import com.training.librarywaitlist.model.MemberType;
import com.training.librarywaitlist.model.WaitlistRequest;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

public class WaitlistService {

    /*
     * JUSTIFICATION: ArrayDeque
     * 1. Pure FIFO: O(1) amortized enqueue (offer) and dequeue (poll) operations.
     * 2. Memory Efficient: Backed by a resizable ring buffer array; avoids creating wrapper Node
     *    objects on every enqueue (unlike LinkedList), minimizing GC churn.
     */
    private final Deque<String> standardWaitlist = new ArrayDeque<>();

    /*
     * JUSTIFICATION: PriorityQueue
     * 1. Heap Scheduling: Binary min-heap guarantees O(log n) enqueue and O(log n) dequeue.
     * 2. Priority Rules: Elevates Faculty requests before Students, falling back to insertion timestamp.
     */
    private final Queue<WaitlistRequest> facultyPriorityQueue = new PriorityQueue<>();

    public void enqueueStandard(String studentName) {
        standardWaitlist.offer(studentName);
    }

    public String serveNextStandard() {
        return standardWaitlist.poll();
    }

    public void enqueuePriority(String name, MemberType type) {
        facultyPriorityQueue.offer(new WaitlistRequest(name, type));
    }

    public WaitlistRequest serveNextPriority() {
        return facultyPriorityQueue.poll();
    }

    public boolean hasPriorityPending() {
        return !facultyPriorityQueue.isEmpty();
    }

    public Deque<String> getStandardWaitlist() {
        return standardWaitlist;
    }
}