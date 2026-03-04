package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.Family;
import com.expensetracker.domain.model.User;
import com.expensetracker.service.AuthService;
import com.expensetracker.service.DataStore;
import com.expensetracker.service.FamilyService;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.SelectableLabel;
import com.expensetracker.ui.util.Ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FamilyController {

    private final User currentUser;
    private final AuthService authService = new AuthService();
    private final FamilyService familyService = new FamilyService();
    private final VBox container = new VBox(20);

    public FamilyController(User user) {
        // Fetch fresh data when controller is built
        this.currentUser = DataStore.users().get(user.getId()); 
    }

    public Node buildView() {
        container.setPadding(new Insets(10));
        refreshView();
        return container;
    }

    private void refreshView() {
        container.getChildren().clear();
        User freshUser = DataStore.users().get(currentUser.getId()); // Always get latest user data
        Family family = freshUser.getFamilyId() != null ? DataStore.families().get(freshUser.getFamilyId()) : null;
        if (family == null) {
            container.getChildren().add(buildNoFamilyView(freshUser));
        } else {
            container.getChildren().add(buildFamilyInfoView(family));
            if (family.isAdmin(freshUser.getId())) {
                container.getChildren().add(buildAdminView(family));
            }
        }
    }

    private Node buildNoFamilyView(User user) {
        VBox box = new VBox(20);
        if (user.getPendingJoinRequestForFamilyId() != null) {
            VBox pendingCard = new VBox(15);
            Ui.applyCard(pendingCard);
            String familyName = Optional.ofNullable(DataStore.families().get(user.getPendingJoinRequestForFamilyId())).map(Family::getName).orElse("a deleted family");
            pendingCard.getChildren().add(Ui.createLabel("You have a pending request to join '" + familyName + "'.", 14));
            Button cancelBtn = new Button("Cancel Join Request");
            cancelBtn.setStyle(Palette.dangerButton());
            cancelBtn.setOnAction(e -> {
                authService.cancelJoinRequest(user);
                refreshView();
            });
            pendingCard.getChildren().add(cancelBtn);
            box.getChildren().add(pendingCard);
        } else {
            VBox createCard = new VBox(15);
            Ui.applyCard(createCard);
            TextField createNameField = new TextField();
            createNameField.setPromptText("New Family Name");
            Button createBtn = new Button("Create Family");
            createBtn.setOnAction(e -> {
                try {
                    authService.createFamily(user, createNameField.getText());
                    refreshView();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                }
            });
            createCard.getChildren().addAll(Ui.createLabel("Create a new family:"), createNameField, createBtn);

            VBox joinCard = new VBox(15);
            Ui.applyCard(joinCard);
            TextField joinIdField = new TextField();
            joinIdField.setPromptText("Family ID (e.g., FAM-ABCDEFGH)");
            Button joinBtn = new Button("Request to Join Family");
            joinBtn.setOnAction(e -> {
                try {
                    authService.joinFamily(user, joinIdField.getText());
                    refreshView();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                }
            });
            joinCard.getChildren().addAll(Ui.createLabel("Or join an existing one:"), joinIdField, joinBtn);
            box.getChildren().addAll(createCard, joinCard);
        }
        return box;
    }

    private Node buildFamilyInfoView(Family family) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        Ui.applyCard(grid);

        HBox titleBox = new HBox(10);
        Text familyName = new Text(family.getName());
        familyName.setFont(Font.font("System", FontWeight.BOLD, 18));
        familyName.setFill(Color.web(Palette.TEXT_PRIMARY));
        titleBox.getChildren().add(familyName);

        if (family.isAdmin(currentUser.getId())) {
            Label adminLabel = new Label("(You are an admin)");
            adminLabel.setStyle("-fx-text-fill: " + Palette.ACCENT + "; -fx-font-weight: bold;");
            titleBox.getChildren().add(adminLabel);
        }

        String members = family.getMemberUserIds().stream().map(id -> DataStore.users().get(id).getName() + (family.isAdmin(id) ? " (Admin)" : "")).collect(Collectors.joining(", "));
        Text membersText = Ui.createStyledText(members);
        membersText.setWrappingWidth(400);

        TextField familyBudgetField = new TextField(String.valueOf(family.getBudget().getFamilyMonthlyBudget()));
        Button setFamilyBudgetBtn = new Button("Set Budget");
        setFamilyBudgetBtn.setDisable(!family.isAdmin(currentUser.getId()));
        setFamilyBudgetBtn.setOnAction(e -> {
            try {
                familyService.setFamilyBudget(family, Double.parseDouble(familyBudgetField.getText()));
                new Alert(Alert.AlertType.INFORMATION, "Family budget updated!").show();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid budget amount.").show();
            }
        });
        HBox budgetBox = new HBox(10, familyBudgetField, setFamilyBudgetBtn);

        Button leaveBtn = new Button("Leave Family");
        leaveBtn.setStyle(Palette.dangerButton());
        leaveBtn.setOnAction(e -> {
            try {
                authService.leaveFamily(currentUser, family);
                refreshView();
            } catch (IllegalStateException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
            }
        });

        grid.add(titleBox, 0, 0, 2, 1);
        grid.add(Ui.createLabel("Family ID:"), 0, 1);
        grid.add(new SelectableLabel(family.getId()), 1, 1);
        grid.add(Ui.createLabel("Members:"), 0, 2);
        grid.add(membersText, 1, 2);
        grid.add(Ui.createLabel("Family Monthly Budget:"), 0, 3);
        grid.add(budgetBox, 1, 3);
        grid.add(leaveBtn, 1, 4);
        return grid;
    }

    private Node buildAdminView(Family family) {
        VBox adminBox = new VBox(15);
        Ui.applyCard(adminBox);

        Text adminTitle = new Text("Admin Panel");
        adminTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        adminTitle.setFill(Color.web(Palette.TEXT_PRIMARY));

        VBox requestsBox = new VBox(10);
        Set<String> pendingIds = family.getPendingRequestUserIds();

        if (pendingIds.isEmpty()) {
            requestsBox.getChildren().add(Ui.createLabel("No pending join requests."));
        } else {
            pendingIds.forEach(userId -> {
                User requestingUser = DataStore.users().get(userId);
                HBox reqRow = new HBox(10);
                reqRow.setAlignment(Pos.CENTER_LEFT);

                Button approve = new Button("Approve");
                Button deny = new Button("Deny");

                approve.setOnAction(e -> {
                    authService.approveRequest(requestingUser, family);
                    refreshView();
                });
                deny.setOnAction(e -> {
                    authService.denyRequest(requestingUser, family);
                    refreshView();
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                reqRow.getChildren().addAll(Ui.createLabel(requestingUser.getName()), spacer, approve, deny);
                requestsBox.getChildren().add(reqRow);
            });
        }
        adminBox.getChildren().addAll(adminTitle, new Separator(), Ui.createLabel("Pending Join Requests:"), requestsBox);
        return adminBox;
    }
}