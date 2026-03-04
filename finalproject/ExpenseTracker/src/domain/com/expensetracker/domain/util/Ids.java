package com.expensetracker.domain.util;

import java.security.SecureRandom;

public class Ids {

    private static final SecureRandom r = new SecureRandom();
    private static final String alpha = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String newId(String p) {
        StringBuilder s = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            s.append(alpha.charAt(r.nextInt(alpha.length())));
        }
        return p + "-" + s;
    }
}