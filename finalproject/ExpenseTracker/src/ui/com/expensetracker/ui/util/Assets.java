package com.expensetracker.ui.util;

import javafx.scene.image.Image;

import java.io.File;

public class Assets {

    private static final String path = "miniproject/assets/icons/";

    public static Image load(String n) {
        File f = new File(path + n);
        return f.exists() ? new Image(f.toURI().toString()) : null;
    }
}