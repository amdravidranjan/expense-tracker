package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.ReportService;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class ReportsController {

    private final ExpenseService expenseService;
    private final User currentUser;

    public ReportsController(ExpenseService s, User u) {
        this.expenseService = s;
        this.currentUser = u;
    }

    public Node buildView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(10));

        List<Expense> expenses = expenseService.listPersonalByUserMonth(currentUser.getId(), YearMonth.now());
        if (expenses.isEmpty()) {
            container.getChildren().add(Ui.createLabel("No personal expenses this month to generate a report."));
            return container;
        }

        PieChart chart = buildCategoryPieChart(expenses);
        Button exportButton = new Button("Export Personal Report to Text File");
        exportButton.setStyle(Palette.primaryButton());
        exportButton.setOnAction(e -> exportReport(expenses));

        container.getChildren().addAll(chart, exportButton);
        return container;
    }

    private PieChart buildCategoryPieChart(List<Expense> expenses) {
        Map<Category, Double> totals = expenseService.totalsByCategory(expenses);
        PieChart chart = new PieChart();
        totals.forEach((cat, total) -> chart.getData().add(new PieChart.Data(cat.toString() + " (" + Ui.currency(total) + ")", total)));
        chart.setTitle("Current Month's Personal Expenses by Category");
        chart.setStyle(Palette.chartStyle());
        return chart;
    }

    private void exportReport(List<Expense> expenses) {
        YearMonth month = YearMonth.now();
        Path path = Paths.get("miniproject", "data", "report-" + currentUser.getUsername() + "-" + month + ".txt");
        try {
            new ReportService().exportToText(path, month, expenses);
            new Alert(Alert.AlertType.INFORMATION, "Report successfully exported to:\n" + path.toAbsolutePath()).show();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to export report: " + e.getMessage()).show();
        }
    }
}