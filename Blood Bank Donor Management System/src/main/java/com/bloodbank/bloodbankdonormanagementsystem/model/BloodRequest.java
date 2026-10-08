package com.bloodbank.bloodbankdonormanagementsystem.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class BloodRequest {
    private static int requestCounter = 7000;

    private final String requestId;
    private final String hospitalName;
    private final String bloodGroup;
    private final String componentType;
    private final int unitsRequired;
    private final String urgency; // ROUTINE, URGENT, STAT
    private final LocalDateTime requestedAt;
    private String status;        // PENDING, FULFILLED, CANCELLED

    // Chained Constructor 1 (Defaults to ROUTINE)
    public BloodRequest(String hospitalName, String bloodGroup, String componentType, int unitsRequired) {
        this(hospitalName, bloodGroup, componentType, unitsRequired, "ROUTINE");
    }

    // Chained Constructor 2 (Full parameters with validations)
    public BloodRequest(String hospitalName, String bloodGroup, String componentType, int unitsRequired, String urgency) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hospital name cannot be blank");
        }
        if (unitsRequired <= 0) {
            throw new IllegalArgumentException("Units required must be greater than 0");
        }

        this.requestId = "REQ-" + (++requestCounter);
        this.hospitalName = hospitalName.trim();
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group cannot be null");
        this.componentType = Objects.requireNonNull(componentType, "Component type cannot be null");
        this.unitsRequired = unitsRequired;
        this.urgency = Objects.requireNonNull(urgency, "Urgency cannot be null");
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // NFR-P3: Urgent priority flag
    public boolean isHighPriority() {
        return "STAT".equalsIgnoreCase(this.urgency) || "URGENT".equalsIgnoreCase(this.urgency);
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public String getHospitalName() { return hospitalName; }
    public String getBloodGroup() { return bloodGroup; }
    public String getComponentType() { return componentType; }
    public int getUnitsRequired() { return unitsRequired; }
    public String getUrgency() { return urgency; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = Objects.requireNonNull(status, "Status cannot be null"); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodRequest that = (BloodRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }

    @Override
    public String toString() {
        return "BloodRequest{id='" + requestId + "', hospital='" + hospitalName + "', group='" + bloodGroup + "', units=" + unitsRequired + ", urgency='" + urgency + "', status='" + status + "'}";
    }
}