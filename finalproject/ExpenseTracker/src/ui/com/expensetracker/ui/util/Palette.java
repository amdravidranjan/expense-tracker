package com.expensetracker.ui.util;

public class Palette {

    public static final String PRIMARY_DARK = "#2C3E50", ACCENT = "#1ABC9C", ACCENT_LIGHT = "#A3E4D7", BACKGROUND_LIGHT = "#ECF0F1", BACKGROUND_DARK = "#3D566E", TEXT_PRIMARY = "#2C3E50", DANGER = "#E74C3C";
    public static final String GRADIENT_1 = "linear-gradient(to right, #1ABC9C, #16A085)", GRADIENT_2 = "linear-gradient(to right, #3498DB, #2980B9)", GRADIENT_3 = "linear-gradient(to right, #9B59B6, #8E44AD)", GRADIENT_BLUE_BG = "linear-gradient(to top right, #34495E, #2C3E50)";

    public static String card() {
        return "-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20;";
    }

    public static String primaryButton() {
        return "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    }

    public static String secondaryButton() {
        return "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: " + ACCENT_LIGHT + "; -fx-border-radius: 5; -fx-border-width: 1px;";
    }

    public static String secondaryButtonHover() {
        return "-fx-background-color: " + PRIMARY_DARK + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: " + ACCENT_LIGHT + "; -fx-border-radius: 5; -fx-border-width: 1px;";
    }

    public static String dangerButton() {
        return "-fx-background-color: " + DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;";
    }

    public static String chartStyle() {
        return ".chart-title { -fx-text-fill: " + TEXT_PRIMARY + "; } .chart-pie-label { -fx-fill: " + TEXT_PRIMARY + "; } .chart-legend { -fx-text-fill: " + TEXT_PRIMARY + "; } .axis-label { -fx-text-fill: " + TEXT_PRIMARY + "; } .axis { -fx-tick-label-fill: " + TEXT_PRIMARY + "; }";
    }
}