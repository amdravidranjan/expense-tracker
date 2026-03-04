package com.expensetracker.service;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.util.Ids;
import com.expensetracker.domain.util.Validation;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseService {

    public Expense add(String uId, String fId, LocalDate d, Category c, double a, String r) {
        Validation.requireNonNegative(a, "Amount");
        Expense e = new Expense(Ids.newId("EXP"), uId, fId, d, c, a, r);
        DataStore.expenses().put(e.getId(), e);
        DataStore.saveExpenses();
        return e;
    }

    public void update(Expense e) {
        DataStore.expenses().put(e.getId(), e);
        DataStore.saveExpenses();
    }

    public void delete(Expense e) {
        DataStore.expenses().remove(e.getId());
        DataStore.saveExpenses();
    }

    public List<Expense> listPersonalByUserMonth(String uId, YearMonth m) {
        return DataStore.expenses().values().stream()
                .filter(e -> e.getUserId().equals(uId) && e.getFamilyId() == null && YearMonth.from(e.getDate()).equals(m))
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Expense> listByFamilyMonth(String fId, YearMonth m) {
        if (fId == null) {
            return Collections.emptyList();
        }
        return DataStore.expenses().values().stream()
                .filter(e -> fId.equals(e.getFamilyId()) && YearMonth.from(e.getDate()).equals(m))
                .collect(Collectors.toList());
    }

    public Map<String, Double> memberContributionsForMonth(String fId, YearMonth m) {
        return listByFamilyMonth(fId, m).stream().collect(Collectors.groupingBy(Expense::getUserId, Collectors.summingDouble(Expense::getAmount)));
    }

    public double total(List<Expense> e) {
        return e.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<Category, Double> totalsByCategory(List<Expense> e) {
        return e.stream().collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
    }
}