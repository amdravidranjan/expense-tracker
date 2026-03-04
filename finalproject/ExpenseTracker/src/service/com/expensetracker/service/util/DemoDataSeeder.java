package com.expensetracker.service.util;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;
import com.expensetracker.service.AuthService;
import com.expensetracker.service.DataStore;
import com.expensetracker.service.ExpenseService;

import java.time.LocalDate;

public class DemoDataSeeder {

    public static void seedIfEmpty() {
        if (DataStore.users().isEmpty()) {
            AuthService a = new AuthService();
            ExpenseService e = new ExpenseService();
            User u = a.register("Demo User", "demo", "demo");
            Family f = a.createFamily(u, "The Demo Family");
            e.add(u.getId(), null, LocalDate.now().minusDays(5), Category.GROCERIES, 120.50, "Shop");
            e.add(u.getId(), f.getId(), LocalDate.now().minusDays(3), Category.UTILITIES, 55.00, "Bill");
            e.add(u.getId(), null, LocalDate.now().minusDays(2), Category.TRANSPORT, 30.25, "Gas");
            e.add(u.getId(), f.getId(), LocalDate.now().minusDays(1), Category.ENTERTAINMENT, 45.00, "Movie");
            System.out.println("Demo data seeded. Login: demo / demo");
        }
    }
}