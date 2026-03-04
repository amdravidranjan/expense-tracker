package com.expensetracker.ui.util;

import javafx.scene.control.TextField;

public class SelectableLabel extends TextField {

    public SelectableLabel(String t) {
        super(t);
        setEditable(false);
        setStyle("-fx-background-color: transparent; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
    }
}