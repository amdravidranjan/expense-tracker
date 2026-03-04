package com.expensetracker.ui.util;

import com.expensetracker.domain.util.CurrencyUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Ui {

    public static void applyCard(Region r) {
        r.setStyle(Palette.card());
        addShadow(r);
    }

    public static void addShadow(Node n) {
        DropShadow s = new DropShadow();
        s.setColor(Color.rgb(0, 0, 0, 0.1));
        s.setRadius(10);
        s.setOffsetX(0);
        s.setOffsetY(4);
        n.setEffect(s);
    }

    // public static String currency(double a) {
    //     return String.format("%s %.2f", Symbols.RUPEE, a);
    // }

    // A helper method for quick access in UI code
    public static String currency(double a) {
        return CurrencyUtil.currency(a);
    }
    
    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(Palette.TEXT_PRIMARY));
        return label;
    }

    public static Label createLabel(String text, double fontSize) {
        Label label = createLabel(text);
        label.setFont(Font.font(fontSize));
        return label;
    }

    public static RadioButton createRadioButton(String text) {
        RadioButton rb = new RadioButton(text);
        rb.setTextFill(Color.web(Palette.TEXT_PRIMARY));
        return rb;
    }

    public static Text createStyledText(String content) {
        Text text = new Text(content);
        text.setFill(Color.web(Palette.TEXT_PRIMARY));
        return text;
    }
}