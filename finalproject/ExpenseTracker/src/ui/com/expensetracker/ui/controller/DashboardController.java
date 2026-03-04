package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Category;
import com.expensetracker.domain.model.Expense;
import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;
import com.expensetracker.domain.util.DateUtil;
import com.expensetracker.service.AuthService;
import com.expensetracker.service.DataStore;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.FamilyService;
import com.expensetracker.ui.navigation.SceneRouter;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardController {
    // ... (Keep all existing methods, just update imports)
    // IMPORTANT: Keep the original code logic exactly as it was.
    private final SceneRouter sceneRouter;
    private final User currentUser;
    private final ExpenseService expenseService = new ExpenseService();
    private final BorderPane root = new BorderPane();

    public DashboardController(SceneRouter sr, User u) {
        this.sceneRouter = sr;
        this.currentUser = u;
    }

    public Scene buildView() {
        root.setTop(buildHeader());
        root.setLeft(buildSidebar());
        showDashboard();
        return new Scene(root, 1280, 800);
    }

    private void setCenterContent(Node content) {
        root.setCenter(content);
    }

    private Node buildHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + Palette.PRIMARY_DARK + ";");
        Text title = new Text("Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setFill(Color.WHITE);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Text welcome = new Text("Welcome, " + currentUser.getName());
        welcome.setFont(Font.font("System", 16));
        welcome.setFill(Color.WHITE);
        header.getChildren().addAll(title, spacer, welcome);
        return header;
    }

    private Node buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20, 15, 20, 15));
        sidebar.setMinWidth(220);
        sidebar.setStyle("-fx-background-color: " + Palette.BACKGROUND_DARK + ";");

        sidebar.getChildren().addAll(
                createSidebarButton("Dashboard", this::showDashboard),
                createSidebarButton("Add Expense", this::showAddExpenseForm),
                createSidebarButton("Family Management", this::showFamilyView),
                createSidebarButton("Reports", this::showReportsView),
                createSidebarButton("History", this::showHistoryView),
                createSidebarButton("Settings", this::showSettingsView),
                createSidebarButton("Help", this::showHelpView),
                new Separator(),
                createSidebarButton("Logout", () -> {
                    LoginController loginController = new LoginController(sceneRouter);
                    sceneRouter.setScene(loginController.buildView());
                    sceneRouter.getPrimaryStage().setMaximized(false);
                })
        );
        return sidebar;
    }

    private void showViewInContainer(String title, Node content) {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: " + Palette.BACKGROUND_LIGHT);
        Text contentTitle = new Text(title);
        contentTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        contentTitle.setFill(Color.web(Palette.TEXT_PRIMARY));
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        container.getChildren().addAll(contentTitle, scrollPane);
        setCenterContent(container);
    }

    private void showDashboard() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab personalTab = new Tab("Personal Dashboard", buildPersonalDashboardTab());
        Tab familyTab = new Tab("Family Dashboard", buildFamilyDashboardTab());
        if (currentUser.getFamilyId() == null) {
            familyTab.setDisable(true);
        }
        tabPane.getTabs().addAll(personalTab, familyTab);
        setCenterContent(tabPane);
    }

    private void showAddExpenseForm() {
        showViewInContainer("Add New Expense", new ExpenseFormController(expenseService, currentUser, this::showDashboard).buildView());
    }

    private void showFamilyView() {
        showViewInContainer("Family Management", new FamilyController(currentUser).buildView());
    }

    private void showReportsView() {
        showViewInContainer("Monthly Reports", new ReportsController(expenseService, currentUser).buildView());
    }

    private void showHistoryView() {
        showViewInContainer("Archived Expense History", new HistoryController().buildView());
    }

    private void showSettingsView() {
        showViewInContainer("Settings", new SettingsController(currentUser).buildView());
    }

    private void showHelpView() {
        showViewInContainer("Help & Documentation", new HelpController().buildView());
    }

    private Node buildPersonalDashboardTab() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + Palette.BACKGROUND_LIGHT);
        HBox kpiContainer = new HBox(20);
        List<Expense> userExpenses = expenseService.listPersonalByUserMonth(currentUser.getId(), YearMonth.now());
        double personalTotal = expenseService.total(userExpenses);
        double personalBudget = currentUser.getPersonalMonthlyBudget();
        kpiContainer.getChildren().addAll(createKpiCard("Personal Budget", Ui.currency(personalBudget), Palette.GRADIENT_1), createKpiCard("Spent This Month", Ui.currency(personalTotal), Palette.GRADIENT_2), createKpiCard("Remaining", Ui.currency(personalBudget - personalTotal), Palette.GRADIENT_3));
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        VBox personalPanel = createTitledCard("Your Spending Breakdown", buildPersonalPieChart(userExpenses));
        VBox recentExpensesPanel = createTitledCard("Recent Personal Expenses", buildRecentExpensesTable());
        grid.add(personalPanel, 0, 0);
        grid.add(recentExpensesPanel, 1, 0);
        GridPane.setHgrow(personalPanel, Priority.ALWAYS);
        GridPane.setHgrow(recentExpensesPanel, Priority.ALWAYS);
        content.getChildren().addAll(kpiContainer, grid);
        return new ScrollPane(content);
    }

    private Node buildFamilyDashboardTab() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: " + Palette.BACKGROUND_LIGHT);
        Family family = currentUser.getFamilyId() != null ? DataStore.families().get(currentUser.getFamilyId()) : null;
        if (family == null) {
            content.getChildren().add(Ui.createLabel("Join a family to enable this dashboard.", 18));
            return content;
        }
        List<Expense> familyExpenses = expenseService.listByFamilyMonth(family.getId(), YearMonth.now());
        double familyTotal = expenseService.total(familyExpenses);
        double familyBudget = family.getBudget().getFamilyMonthlyBudget();
        HBox kpiContainer = new HBox(20);
        kpiContainer.getChildren().addAll(createKpiCard("Family Budget", Ui.currency(familyBudget), Palette.GRADIENT_1), createKpiCard("Family Spent", Ui.currency(familyTotal), Palette.GRADIENT_2), createKpiCard("Family Remaining", Ui.currency(familyBudget - familyTotal), Palette.GRADIENT_3));
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        VBox contributionPanel = createTitledCard("Family Member Contributions", buildFamilyBarChart());
        VBox recentFamilyExpensesPanel = createTitledCard("Recent Family Expenses", buildRecentFamilyExpensesTable());
        grid.add(contributionPanel, 0, 0);
        grid.add(recentFamilyExpensesPanel, 1, 0);
        GridPane.setHgrow(contributionPanel, Priority.ALWAYS);
        GridPane.setHgrow(recentFamilyExpensesPanel, Priority.ALWAYS);
        content.getChildren().addAll(kpiContainer, grid);
        return new ScrollPane(content);
    }

    private Button createSidebarButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(Palette.secondaryButton());
        button.setOnMouseEntered(e -> button.setStyle(Palette.secondaryButtonHover()));
        button.setOnMouseExited(e -> button.setStyle(Palette.secondaryButton()));
        button.setOnAction(e -> action.run());
        return button;
    }

    private VBox createTitledCard(String title, Node contentNode) {
        VBox card = new VBox(15);
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleText.setFill(Color.web(Palette.TEXT_PRIMARY));
        card.getChildren().addAll(titleText, contentNode);
        Ui.applyCard(card);
        VBox.setVgrow(contentNode, Priority.ALWAYS);
        return card;
    }

    private Node createKpiCard(String title, String value, String background) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-radius: 8; -fx-padding: 20; -fx-background-color: " + background + ";");
        HBox.setHgrow(card, Priority.ALWAYS);
        Ui.addShadow(card);
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleText.setFill(Color.WHITE);
        Text valueText = new Text(value);
        valueText.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueText.setFill(Color.WHITE);
        card.getChildren().addAll(titleText, valueText);
        return card;
    }

    private Node buildPersonalPieChart(List<Expense> expenses) {
        if (expenses.isEmpty()) {
            return Ui.createLabel("No personal expenses recorded this month.");
        }
        Map<Category, Double> totals = expenseService.totalsByCategory(expenses);
        PieChart chart = new PieChart();
        totals.forEach((cat, total) -> chart.getData().add(new PieChart.Data(cat.toString(), total)));
        chart.setTitle("Personal Expenses by Category");
        chart.setLegendVisible(true);
        chart.setLabelsVisible(false);
        chart.setMinHeight(300);
        chart.setStyle(Palette.chartStyle());
        return chart;
    }

    private Node buildFamilyBarChart() {
        Map<String, Double> contributions = expenseService.memberContributionsForMonth(currentUser.getFamilyId(), YearMonth.now());
        if (contributions.isEmpty()) {
            return Ui.createLabel("No family expenses recorded this month.");
        }
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Contributions");
        barChart.setLegendVisible(false);
        barChart.setMinHeight(300);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        contributions.forEach((userId, total) -> {
            String memberName = DataStore.users().getOrDefault(userId, new User("?", "Unknown", "?", "?")).getName();
            series.getData().add(new XYChart.Data<>(memberName, total));
        });
        barChart.getData().add(series);
        return barChart;
    }

    private TableView<Expense> createEditableTable(Runnable refreshCallback) {
        TableView<Expense> table = new TableView<>();
        table.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Edit");
            MenuItem deleteItem = new MenuItem("Delete");

            editItem.setOnAction(event -> {
                Expense expense = row.getItem();
                Stage modalStage = new Stage();
                modalStage.initModality(Modality.APPLICATION_MODAL);
                modalStage.initOwner(sceneRouter.getPrimaryStage());
                ExpenseEditController editController = new ExpenseEditController(expenseService, expense, modalStage, refreshCallback);
                // COMPILER FIX 1: The Scene constructor needs a Parent, not just a Node.
                Scene scene = new Scene(editController.buildView());
                modalStage.setScene(scene);
                modalStage.setTitle("Edit Expense");
                modalStage.showAndWait();
            });

            deleteItem.setOnAction(event -> {
                Expense expense = row.getItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this expense: " + Ui.currency(expense.getAmount()) + " on " + expense.getCategory() + "?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        expenseService.delete(expense);
                        refreshCallback.run();
                    }
                });
            });

            contextMenu.getItems().addAll(editItem, deleteItem);
            // COMPILER FIX 2: Use Bindings.when() for conditional binding, not map()
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            row.hoverProperty().addListener((obs, old, isNow) -> row.setStyle(isNow && !row.isEmpty() ? "-fx-background-color: #e0f7fa;" : ""));
            return row;
        });
        return table;
    }

    private Node buildRecentExpensesTable() {
        TableView<Expense> table = createEditableTable(this::showDashboard);
        table.setMinHeight(300);
        List<Expense> expenses = expenseService.listPersonalByUserMonth(currentUser.getId(), YearMonth.now());
        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtil.format(cellData.getValue().getDate())));
        TableColumn<Expense, Category> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()));
        TableColumn<Expense, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> new SimpleStringProperty(Ui.currency(cellData.getValue().getAmount())));
        TableColumn<Expense, String> remarksCol = new TableColumn<>("Remarks");
        remarksCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRemarks()));
        table.getColumns().addAll(dateCol, catCol, amountCol, remarksCol);
        List<Expense> recent = expenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed()).limit(10).collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(recent));
        return table;
    }

    private Node buildRecentFamilyExpensesTable() {
        TableView<Expense> table = createEditableTable(this::showDashboard);
        table.setMinHeight(300);
        List<Expense> familyExpenses = expenseService.listByFamilyMonth(currentUser.getFamilyId(), YearMonth.now());
        TableColumn<Expense, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(cellData -> new SimpleStringProperty(DataStore.users().get(cellData.getValue().getUserId()).getName()));
        TableColumn<Expense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtil.format(cellData.getValue().getDate())));
        TableColumn<Expense, Category> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategory()));
        TableColumn<Expense, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> new SimpleStringProperty(Ui.currency(cellData.getValue().getAmount())));
        table.getColumns().addAll(memberCol, dateCol, catCol, amountCol);
        List<Expense> recent = familyExpenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed()).limit(10).collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(recent));
        return table;
    }
}