package com.expensetracker.service;

import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.util.Validation;

public class FamilyService {

    public void setFamilyBudget(Family f, double a) {
        Validation.requireNonNegative(a, "Amount");
        f.getBudget().setFamilyMonthlyBudget(a);
        DataStore.saveFamilies();
    }
}