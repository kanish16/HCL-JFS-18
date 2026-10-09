package com.bloodbank.bloodbankdonormanagementsystem.model;

import java.time.LocalDateTime;

public abstract class BaseEntity {
    private final String id;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseEntity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}