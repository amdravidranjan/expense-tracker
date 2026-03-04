package com.expensetracker.ui.navigation;

import com.expensetracker.domain.model.User;
import com.expensetracker.ui.controller.DashboardController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneRouter {

    private final Stage primaryStage;

    public SceneRouter(Stage stage) {
        this.primaryStage = stage;
    }

    public void goToDashboard(User user) {
        DashboardController controller = new DashboardController(this, user);
        setScene(controller.buildView());
        primaryStage.setMaximized(true);
    }

    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}