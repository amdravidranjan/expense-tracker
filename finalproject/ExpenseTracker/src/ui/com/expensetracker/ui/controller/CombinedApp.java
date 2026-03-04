package com.expensetracker.ui.controller;

import com.expensetracker.service.DataStore;
import com.expensetracker.service.util.DemoDataSeeder;
import com.expensetracker.service.util.RolloverService;
import com.expensetracker.ui.navigation.SceneRouter;
import com.expensetracker.ui.util.Assets;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CombinedApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        DataStore.ensureDataDirectories();
        DataStore.loadAll(); // Initial load
        RolloverService.rolloverIfNeeded(DataStore.expenses().values());
        DemoDataSeeder.seedIfEmpty();

        primaryStage.setTitle("Personal & Family Budget Manager");
        SceneRouter sceneRouter = new SceneRouter(primaryStage);
        LoginController loginController = new LoginController(sceneRouter);

        try {
            Image icon = Assets.load("logo.png");
            if (icon != null) {
                primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application icon.");
        }

        sceneRouter.setScene(loginController.buildView());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}