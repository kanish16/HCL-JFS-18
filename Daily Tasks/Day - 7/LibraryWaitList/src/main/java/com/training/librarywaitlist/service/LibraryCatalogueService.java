package com.training.librarywaitlist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LibraryCatalogueService {

    /*
     * JUSTIFICATION: ArrayList
     * 1. Indexed Lookup: O(1) random read access via get(index), ideal for catalog searching.
     * 2. Cache Locality: Contiguous memory storage minimizes CPU cache misses compared to node references.
     * 3. Amortized O(1) appends for bulk imports.
     */
    private final List<String> rawCatalogue = new ArrayList<>();

    /*
     * JUSTIFICATION: LinkedHashSet
     * 1. De-duplication: Guarantees unique titles with O(1) lookup/insert cost.
     * 2. Predictable Iteration: Retains chronological insertion order, unlike HashSet (unordered)
     *    or TreeSet (O(log n) tree maintenance).
     */
    private final Set<String> deduplicatedCatalogue = new LinkedHashSet<>();

    public void addBook(String title) {
        rawCatalogue.add(title);
        deduplicatedCatalogue.add(title);
    }

    public List<String> getRawCatalogue() {
        return Collections.unmodifiableList(rawCatalogue);
    }

    public Set<String> getDeduplicatedCatalogue() {
        return Collections.unmodifiableSet(deduplicatedCatalogue);
    }
}