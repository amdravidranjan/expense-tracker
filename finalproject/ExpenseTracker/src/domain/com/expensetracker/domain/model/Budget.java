package com.expensetracker.domain.model;

import java.io.Serializable;

public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;
    private double familyBudget = 25000.0;

    public double getFamilyMonthlyBudget() {
        return familyBudget;
    }

    public void setFamilyMonthlyBudget(double b) {
        this.familyBudget = b;
    }
}