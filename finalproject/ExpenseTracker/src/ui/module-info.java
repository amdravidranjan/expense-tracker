module ui {
    requires domain;
    requires service;
    
    // JavaFX requirements
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // Expose controller package to JavaFX runtime for reflection (e.g., launch method)
    opens com.expensetracker.ui.controller to javafx.fxml, javafx.graphics;
    opens com.expensetracker.ui.navigation to javafx.graphics;
    
    exports com.expensetracker.ui.controller;
}