package com.expensetracker.service;

import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.util.CurrencyUtil;
import com.expensetracker.domain.util.DateUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

public class ReportService {

    public void exportToText(Path path, YearMonth m, List<Expense> e) throws IOException {
        StringBuilder r = new StringBuilder("Expense Report for " + m + "\n=================================================\n\n");
        // Use CurrencyUtil instead of Ui
        r.append("Total Expenses: ").append(CurrencyUtil.currency(new ExpenseService().total(e))).append("\n\n");
        r.append("Breakdown by Category:\n");
        // Use CurrencyUtil instead of Ui
        new ExpenseService().totalsByCategory(e).forEach((cat, amt) -> r.append(String.format("- %-15s: %s\n", cat, CurrencyUtil.currency(amt))));
        r.append("\n\nAll Expenses:\n");
        // Use CurrencyUtil instead of Ui
        e.stream().sorted(java.util.Comparator.comparing(Expense::getDate)).forEach(exp -> r.append(String.format("- %s | %-15s | %-10s | %s\n", DateUtil.format(exp.getDate()), exp.getCategory(), CurrencyUtil.currency(exp.getAmount()), exp.getRemarks())));
        Files.write(path, r.toString().getBytes(StandardCharsets.UTF_8));
    }
}