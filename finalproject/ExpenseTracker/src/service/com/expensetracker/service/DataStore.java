package com.expensetracker.service;

import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataStore {

    private static final Map<String, User> u = new HashMap<>();
    private static final Map<String, Family> f = new HashMap<>();
    private static final Map<String, Expense> e = new HashMap<>();
    private static final Path dir = Paths.get("miniproject", "data"), hist = dir.resolve("history"), uFile = dir.resolve("users.dat"), fFile = dir.resolve("families.dat"), eFile = dir.resolve("expenses.dat");

    public static Map<String, User> users() {
        return u;
    }

    public static Map<String, Family> families() {
        return f;
    }

    public static Map<String, Expense> expenses() {
        return e;
    }

    public static void ensureDataDirectories() {
        try {
            Files.createDirectories(hist);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static void loadAll() {
        load(uFile, u);
        load(fFile, f);
        load(eFile, e);
        System.out.println("Data reloaded from disk.");
    }

    @SuppressWarnings("unchecked")
    private static <T> void load(Path p, Map<String, T> m) {
        if (Files.exists(p)) try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(p.toFile()))) {
            m.clear();
            m.putAll((Map<String, T>) o.readObject());
        } catch (Exception ex) {
            m.clear();
        }
    }

    public static synchronized void saveUsers() {
        save(uFile, u);
    }

    public static synchronized void saveFamilies() {
        save(fFile, f);
    }

    public static synchronized void saveExpenses() {
        save(eFile, e);
    }

    private static synchronized <T> void save(Path p, Map<String, T> m) {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(p.toFile()))) {
            o.writeObject(m);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void archiveMonth(YearMonth m, Collection<Expense> toArchive) {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(hist.resolve("archive-" + m + ".dat").toFile()))) {
            o.writeObject(new ArrayList<>(toArchive));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}