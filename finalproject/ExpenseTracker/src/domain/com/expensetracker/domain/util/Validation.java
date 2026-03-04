package com.expensetracker.domain.util;

public class Validation {

    public static void requireNonBlank(String i, String f) {
        if (i == null || i.trim().isEmpty()) {
            throw new IllegalArgumentException(f + " cannot be blank.");
        }
    }

    public static void requireNonNegative(double v, String f) {
        if (v < 0) {
            throw new IllegalArgumentException(f + " cannot be negative.");
        }
    }
}