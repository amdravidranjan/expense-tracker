package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;
import com.expensetracker.domain.util.Validation;
import com.expensetracker.service.DataStore;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.YearMonth;

public class ExpenseFormController {

    private final ExpenseService expenseService;
    private final User currentUser;
    private final Runnable onSaveSuccess;

    public ExpenseFormController(ExpenseService s, User u, Runnable r) {
        this.expenseService = s;
        this.currentUser = u;
        this.onSaveSuccess = r;
    }

    public Node buildView() {
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(10);
        formGrid.setMaxWidth(500);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        ComboBox<Category> categoryBox = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        categoryBox.setPromptText("Select a category");
        categoryBox.setMaxWidth(Double.MAX_VALUE);
        TextField amountField = new TextField();
        amountField.setPromptText("e.g., 45.50");
        TextArea remarksArea = new TextArea();
        remarksArea.setPromptText("Optional notes");
        remarksArea.setPrefHeight(80);
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        Button saveButton = new Button("Save Expense");
        saveButton.setStyle(Palette.primaryButton());
        saveButton.setMaxWidth(Double.MAX_VALUE);

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton personalRadio = Ui.createRadioButton("Personal");
        personalRadio.setToggleGroup(typeGroup);
        personalRadio.setSelected(true);
        RadioButton familyRadio = Ui.createRadioButton("Family");
        familyRadio.setToggleGroup(typeGroup);
        if (currentUser.getFamilyId() == null) {
            familyRadio.setDisable(true);
        }
        HBox radioBox = new HBox(20, personalRadio, familyRadio);

        formGrid.add(Ui.createLabel("Expense Type:"), 0, 0);
        formGrid.add(radioBox, 1, 0);
        formGrid.add(Ui.createLabel("Date:"), 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(Ui.createLabel("Category:"), 0, 2);
        formGrid.add(categoryBox, 1, 2);
        formGrid.add(Ui.createLabel("Amount:"), 0, 3);
        formGrid.add(amountField, 1, 3);
        formGrid.add(Ui.createLabel("Remarks:"), 0, 4);
        formGrid.add(remarksArea, 1, 4);
        formGrid.add(saveButton, 1, 5);
        formGrid.add(errorLabel, 1, 6);

        saveButton.setOnAction(e -> handleSave(datePicker, categoryBox, amountField, remarksArea, errorLabel, typeGroup));
        VBox container = new VBox(formGrid);
        Ui.applyCard(container);
        return container;
    }

    private void handleSave(DatePicker d, ComboBox<Category> c, TextField a, TextArea r, Label err, ToggleGroup tg) {
        try {
            err.setText("");
            LocalDate date = d.getValue();
            Category category = c.getValue();
            double amount = Double.parseDouble(a.getText());
            String remarks = r.getText();

            Validation.requireNonNegative(amount, "Amount");
            if (date == null || category == null) {
                throw new IllegalArgumentException("Date and Category are required.");
            }

            boolean isFamilyExpense = ((RadioButton) tg.getSelectedToggle()).getText().equals("Family");
            String familyId = isFamilyExpense ? currentUser.getFamilyId() : null;

            expenseService.add(currentUser.getId(), familyId, date, category, amount, remarks);
            new Alert(Alert.AlertType.INFORMATION, "Expense added successfully!").showAndWait();

            checkAndWarnBudget(isFamilyExpense);
            onSaveSuccess.run();
        } catch (NumberFormatException ex) {
            err.setText("Invalid amount. Please enter a number.");
        } catch (IllegalArgumentException ex) {
            err.setText(ex.getMessage());
        }
    }

    private void checkAndWarnBudget(boolean isFamilyExpense) {
        YearMonth currentMonth = YearMonth.now();
        if (isFamilyExpense) {
            Family family = DataStore.families().get(currentUser.getFamilyId());
            double budget = family.getBudget().getFamilyMonthlyBudget();
            double total = expenseService.total(expenseService.listByFamilyMonth(family.getId(), currentMonth));
            if (total > budget) {
                new Alert(Alert.AlertType.WARNING, "Warning: The family budget of " + Ui.currency(budget) + " has been exceeded this month. Total spending is now " + Ui.currency(total) + ".").show();
            }
        } else {
            double budget = currentUser.getPersonalMonthlyBudget();
            double total = expenseService.total(expenseService.listPersonalByUserMonth(currentUser.getId(), currentMonth));
            if (total > budget) {
                new Alert(Alert.AlertType.WARNING, "Warning: Your personal budget of " + Ui.currency(budget) + " has been exceeded this month. Total spending is now " + Ui.currency(total) + ".").show();
            }
        }
    }
}