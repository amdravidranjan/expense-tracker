package com.expensetracker.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String id, userId;
    private String familyId;
    private String remarks;
    private LocalDate date;
    private Category category;
    private double amount;

    public Expense(String i, String u, String f, LocalDate d, Category c, double a, String r) {
        this.id = i;
        this.userId = u;
        this.familyId = f;
        this.date = d;
        this.category = c;
        this.amount = a;
        this.remarks = r;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setDate(LocalDate d) {
        this.date = d;
    }

    public void setCategory(Category c) {
        this.category = c;
    }

    public void setAmount(double a) {
        this.amount = a;
    }

    public void setRemarks(String r) {
        this.remarks = r;
    }
}