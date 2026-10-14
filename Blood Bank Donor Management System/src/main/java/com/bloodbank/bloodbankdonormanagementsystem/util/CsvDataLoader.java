package com.bloodbank.bloodbankdonormanagementsystem.util;

import com.bloodbank.bloodbankdonormanagementsystem.model.BloodUnit;
import com.bloodbank.bloodbankdonormanagementsystem.model.Donor;
import com.bloodbank.bloodbankdonormanagementsystem.repository.CrudRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Objects;

public final class CsvDataLoader {

    private CsvDataLoader() {
        // Utility class
    }

    public static void loadDonorsFromCsv(String resourcePath, CrudRepository<Donor, String> donorRepo) {
        try (InputStream is = CsvDataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("[WARN] Seed file not found: " + resourcePath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
                int count = 0;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] tokens = line.split(",");
                    if (tokens.length >= 6) {
                        String name = tokens[0].trim();
                        int age = Integer.parseInt(tokens[1].trim());
                        double weight = Double.parseDouble(tokens[2].trim());
                        String bloodGroup = tokens[3].trim().toUpperCase();
                        String phone = tokens[4].trim();
                        int daysAgo = Integer.parseInt(tokens[5].trim());

                        LocalDate lastDonation = (daysAgo > 0) ? LocalDate.now().minusDays(daysAgo) : null;
                        Donor donor = new Donor(name, age, weight, bloodGroup, phone, lastDonation);
                        donorRepo.save(donor);
                        count++;
                    }
                }
                System.out.println("[INIT] Loaded " + count + " donors from " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load donors from CSV: " + e.getMessage());
        }
    }

    public static void loadBloodUnitsFromCsv(String resourcePath, CrudRepository<BloodUnit, String> unitRepo) {
        try (InputStream is = CsvDataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.out.println("[WARN] Seed file not found: " + resourcePath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
                int count = 0;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] tokens = line.split(",");
                    if (tokens.length >= 4) {
                        String donorId = tokens[0].trim();
                        String bloodGroup = tokens[1].trim().toUpperCase();
                        String component = tokens[2].trim().toUpperCase();
                        int daysAgo = Integer.parseInt(tokens[3].trim());

                        LocalDate collectionDate = LocalDate.now().minusDays(daysAgo);
                        BloodUnit unit = new BloodUnit(donorId, bloodGroup, component, collectionDate);
                        unitRepo.save(unit);
                        count++;
                    }
                }
                System.out.println("[INIT] Loaded " + count + " blood units from " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load blood units from CSV: " + e.getMessage());
        }
    }
}