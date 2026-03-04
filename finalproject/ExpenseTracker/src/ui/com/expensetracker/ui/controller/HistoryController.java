package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Expense;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.ui.util.Ui;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistoryController {

    public Node buildView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(10));

        Path historyDir = Paths.get("miniproject", "data", "history");
        Label totalLabel = Ui.createLabel("Total for selected month: -");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        ListView<Path> fileList = new ListView<>();

        try (Stream<Path> files = Files.list(historyDir)) {
            List<Path> archives = files.filter(p -> p.toString().endsWith(".dat")).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            if (archives.isEmpty()) {
                container.getChildren().add(Ui.createLabel("No archived history found."));
                return container;
            }
            fileList.setItems(FXCollections.observableArrayList(archives));
            fileList.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Path item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getFileName().toString());
                        setTextFill(Color.BLACK);
                    }
                }
            });
        } catch (IOException e) {
            container.getChildren().add(Ui.createLabel("Error reading history directory."));
            return container;
        }

        fileList.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(val.toFile()))) {
                    @SuppressWarnings("unchecked")
                    List<Expense> expenses = (List<Expense>) ois.readObject();
                    double total = new ExpenseService().total(expenses);
                    totalLabel.setText("Total for selected month: " + Ui.currency(total));
                } catch (Exception ex) {
                    totalLabel.setText("Error reading archive file.");
                }
            }
        });

        container.getChildren().addAll(Ui.createLabel("Select an archive file to view its total:"), fileList, totalLabel);
        return container;
    }
}