package com.expensetracker.domain.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static String format(LocalDate d) {
        return d.format(fmt);
    }

    public static YearMonth currentMonth() {
        return YearMonth.now();
    }
}