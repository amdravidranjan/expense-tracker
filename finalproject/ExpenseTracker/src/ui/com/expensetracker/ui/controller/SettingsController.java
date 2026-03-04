package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.User;
import com.expensetracker.service.BudgetService;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsController {

    private final User currentUser;
    private final BudgetService budgetService = new BudgetService();

    public SettingsController(User user) {
        this.currentUser = user;
    }

    public Node buildView() {
        VBox container = new VBox(15);
        Ui.applyCard(container);
        container.setMaxWidth(400);

        Label title = Ui.createLabel("Personal Budget Settings", 16);
        title.setFont(Font.font("System", FontWeight.BOLD, 16));

        TextField budgetField = new TextField(String.valueOf(currentUser.getPersonalMonthlyBudget()));
        Button saveButton = new Button("Save Personal Budget");
        saveButton.setStyle(Palette.primaryButton());

        saveButton.setOnAction(e -> {
            try {
                budgetService.setUserBudget(currentUser, Double.parseDouble(budgetField.getText()));
                new Alert(Alert.AlertType.INFORMATION, "Personal budget updated successfully!").show();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid amount. Please enter a number.").show();
            }
        });

        container.getChildren().addAll(title, Ui.createLabel("Your monthly personal budget:"), budgetField, saveButton);
        return container;
    }
}