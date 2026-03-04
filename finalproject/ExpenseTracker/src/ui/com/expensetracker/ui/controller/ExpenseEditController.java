package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Expense;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ExpenseEditController {

    private final ExpenseService expenseService;
    private final Expense expenseToEdit;
    private final Stage stage;
    private final Runnable onSaveSuccess;

    public ExpenseEditController(ExpenseService es, Expense e, Stage s, Runnable r) {
        this.expenseService = es;
        this.expenseToEdit = e;
        this.stage = s;
        this.onSaveSuccess = r;
    }

    // COMPILER FIX 1: Change return type from Node to Parent
    public Parent buildView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + Palette.BACKGROUND_LIGHT);
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(10);

        DatePicker datePicker = new DatePicker(expenseToEdit.getDate());
        ComboBox<Category> categoryBox = new ComboBox<>(javafx.collections.FXCollections.observableArrayList(Category.values()));
        categoryBox.setValue(expenseToEdit.getCategory());
        TextField amountField = new TextField(String.valueOf(expenseToEdit.getAmount()));
        TextArea remarksArea = new TextArea(expenseToEdit.getRemarks());

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle(Palette.primaryButton());
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            try {
                expenseToEdit.setDate(datePicker.getValue());
                expenseToEdit.setCategory(categoryBox.getValue());
                expenseToEdit.setAmount(Double.parseDouble(amountField.getText()));
                expenseToEdit.setRemarks(remarksArea.getText());
                expenseService.update(expenseToEdit);
                onSaveSuccess.run();
                stage.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid data: " + ex.getMessage()).show();
            }
        });
        cancelButton.setOnAction(e -> stage.close());

        formGrid.add(Ui.createLabel("Date:"), 0, 0);
        formGrid.add(datePicker, 1, 0);
        formGrid.add(Ui.createLabel("Category:"), 0, 1);
        formGrid.add(categoryBox, 1, 1);
        formGrid.add(Ui.createLabel("Amount:"), 0, 2);
        formGrid.add(amountField, 1, 2);
        formGrid.add(Ui.createLabel("Remarks:"), 0, 3);
        formGrid.add(remarksArea, 1, 3);

        HBox buttonBar = new HBox(10, saveButton, cancelButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Label title = Ui.createLabel("Editing Expense", 18);
        title.setFont(Font.font("System", FontWeight.BOLD, 18)); // Ensure title style is preserved
        container.getChildren().addAll(title, formGrid, buttonBar);
        return container;
    }
}