package com.expensetracker.ui.controller;

import com.expensetracker.domain.model.User;
import com.expensetracker.service.AuthService;
import com.expensetracker.service.DataStore;
import com.expensetracker.ui.navigation.SceneRouter;
import com.expensetracker.ui.util.Palette;
import com.expensetracker.ui.util.Ui;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginController {

    private final SceneRouter sceneRouter;
    private final AuthService authService;

    // Modified constructor to use a private AuthService instance
    public LoginController(SceneRouter sr) {
        this.sceneRouter = sr;
        this.authService = new AuthService();
    }

    public Scene buildView() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + Palette.GRADIENT_BLUE_BG + ";");

        VBox loginCard = new VBox(15);
        loginCard.setMaxWidth(400);
        loginCard.setPadding(new Insets(30, 40, 30, 40));
        loginCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10;");
        Ui.addShadow(loginCard);

        Text title = new Text("Personal and Family Budget Manager");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setFill(Color.web(Palette.TEXT_PRIMARY));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(new Tab("Login", buildLoginNode()), new Tab("Register", buildRegisterNode()));

        loginCard.getChildren().addAll(title, tabPane);
        root.getChildren().add(loginCard);

        return new Scene(root, 900, 500);
    }

    private Node buildLoginNode() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 0, 0, 0));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(Palette.primaryButton());
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Runnable doLogin = () -> {
            try {
                DataStore.loadAll(); // Force reload of data from disk on login
                User user = authService.login(usernameField.getText(), passwordField.getText());
                sceneRouter.goToDashboard(user);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        };

        loginButton.setOnAction(e -> doLogin.run());
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });
        content.getChildren().addAll(Ui.createLabel("Username"), usernameField, Ui.createLabel("Password"), passwordField, loginButton, errorLabel);
        return content;
    }

    private Node buildRegisterNode() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 0, 0, 0));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button registerButton = new Button("Create Account");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setStyle(Palette.primaryButton());
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Runnable doRegister = () -> {
            try {
                User newUser = authService.register(nameField.getText(), usernameField.getText(), passwordField.getText());
                sceneRouter.goToDashboard(newUser);
            } catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
            }
        };

        registerButton.setOnAction(e -> doRegister.run());
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });

        content.getChildren().addAll(Ui.createLabel("Full Name"), nameField, Ui.createLabel("Username"), usernameField, Ui.createLabel("Password"), passwordField, registerButton, errorLabel);
        return content;
    }
}