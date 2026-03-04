package com.expensetracker.service.util;

import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.util.DateUtil;
import com.expensetracker.service.DataStore;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RolloverService {

    public static void rolloverIfNeeded(Collection<Expense> all) {
        YearMonth last = AppState.getLastActiveMonth(), cur = DateUtil.currentMonth();
        if (last != null && last.isBefore(cur)) {
            List<Expense> toArchive = all.stream().filter(e -> YearMonth.from(e.getDate()).equals(last)).collect(Collectors.toList());
            if (!toArchive.isEmpty()) {
                DataStore.archiveMonth(last, toArchive);
                toArchive.forEach(e -> DataStore.expenses().remove(e.getId()));
                DataStore.saveExpenses();
            }
            AppState.setLastActiveMonth(cur);
            AppState.save();
        }
    }
}