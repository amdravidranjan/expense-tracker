package com.expensetracker.ui.controller;

import com.expensetracker.ui.util.Ui;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class HelpController {

    public Node buildView() {
        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(
                createHelpPane("Getting Started", "Register a new account or use the demo login (demo/demo).\nThe application is split into a Personal and a Family dashboard."),
                createHelpPane("Personal vs. Family Expenses", "Personal expenses count ONLY against your personal budget.\nFamily expenses are shared and count against the overall family budget.\nWhen adding an expense, you MUST choose which type it is."),
                createHelpPane("Managing Budgets", "To set your PERSONAL budget, go to the 'Settings' page.\nTo set the FAMILY budget, you must be a family admin and use the input on the 'Family Management' page."),
                createHelpPane("Editing & Deleting", "You can edit or delete any expense by RIGHT-CLICKING on it in the 'Recent Expenses' tables on the dashboard."),
                createHelpPane("How to Create a Family", "1. Go to 'Family Management'.\n2. Enter a name for your new family and click 'Create'.\n3. You will automatically become the admin of this new family.\n4. Note the 'Family ID'. You must share this ID with others so they can join."),
                createHelpPane("How to Join a Family", "1. Get the 'Family ID' from an existing member.\n2. Go to 'Family Management'.\n3. Enter the ID and click 'Request to Join'.\n4. An admin of that family must approve your request. While your request is pending, you cannot create or join another family."),
                createHelpPane("Admin Responsibilities", "As a family admin, you can:\n- Approve or deny join requests from new members.\n- Set the overall monthly family budget."));
        return accordion;
    }

    private TitledPane createHelpPane(String title, String content) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Text contentText = Ui.createStyledText(content);
        contentText.setWrappingWidth(600);
        layout.getChildren().add(contentText);
        TitledPane pane = new TitledPane(title, layout);
        pane.setAnimated(true);
        return pane;
    }
}