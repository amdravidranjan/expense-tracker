package com.expensetracker.service;

import com.expensetracker.domain.model.User;
import com.expensetracker.domain.util.Validation;

public class BudgetService {

    public void setUserBudget(User u, double a) {
        Validation.requireNonNegative(a, "Amount");
        u.setPersonalMonthlyBudget(a);
        DataStore.saveUsers();
    }
}