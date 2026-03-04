package com.expensetracker.service.util;

import com.expensetracker.domain.util.DateUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;

public class AppState {

    private static final Path file = Paths.get("miniproject", "data", "appstate.dat");
    private static YearMonth lastMonth;

    public static YearMonth getLastActiveMonth() {
        if (lastMonth == null) {
            if (Files.exists(file)) {
                try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(file.toFile()))) {
                    lastMonth = (YearMonth) o.readObject();
                } catch (Exception e) {
                    lastMonth = YearMonth.now();
                }
            } else {
                lastMonth = YearMonth.now();
            }
        }
        return lastMonth;
    }

    public static void setLastActiveMonth(YearMonth m) {
        lastMonth = m;
    }

    public static void save() {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            o.writeObject(lastMonth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}