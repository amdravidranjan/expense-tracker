package com.expensetracker.domain.model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id, name, username, passwordHash;
    private String familyId, pendingJoinRequestForFamilyId;
    private double personalBudget = 15000.0;

    public User(String i, String n, String u, String p) {
        this.id = i;
        this.name = n;
        this.username = u;
        this.passwordHash = p;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFamilyId() {
        return familyId;
    }

    public double getPersonalMonthlyBudget() {
        return personalBudget;
    }

    public String getPendingJoinRequestForFamilyId() {
        return pendingJoinRequestForFamilyId;
    }

    public void setFamilyId(String fId) {
        this.familyId = fId;
    }

    public void setPersonalMonthlyBudget(double b) {
        this.personalBudget = b;
    }

    public void setPendingJoinRequestForFamilyId(String fId) {
        this.pendingJoinRequestForFamilyId = fId;
    }
}