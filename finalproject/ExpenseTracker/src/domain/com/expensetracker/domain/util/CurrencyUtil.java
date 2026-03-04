package com.expensetracker.domain.util;

// Contains the currency symbol logic previously in Ui.java
class Symbols {
    public static final String RUPEE = "\u20B9";
}

// Contains the currency formatting logic previously in Ui.java
public class CurrencyUtil {
    public static String currency(double a) {
        return String.format("%s %.2f", Symbols.RUPEE, a);
    }
}