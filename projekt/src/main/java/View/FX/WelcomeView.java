package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;

public class WelcomeView {
    private Stage stage;
    private DictionaryControllerFX controller;

    public WelcomeView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        StackPane layout = new StackPane();

        Button continueButton = new Button("Przejdź do menu głównego");
        Text welcomeText = new Text("Aplikacja słownikowa");
        Text name = new Text("Fishki");
        welcomeText.getStyleClass().add("welcome-message"); // Apply CSS style
        name.getStyleClass().add("name-message"); // Apply CSS style
        welcomeText.setFill(javafx.scene.paint.Color.WHITE);
        name.setFill(javafx.scene.paint.Color.WHITE);

        continueButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().addAll(welcomeText, name, continueButton);

        StackPane.setAlignment(welcomeText, javafx.geometry.Pos.TOP_CENTER); // Align the welcome text to the top
        StackPane.setAlignment(name, javafx.geometry.Pos.TOP_CENTER); // Align the welcome text to the top
        StackPane.setAlignment(continueButton, javafx.geometry.Pos.BOTTOM_CENTER);

        // Optional: Adjust margins to fine-tune the positioning of the elements
        StackPane.setMargin(welcomeText, new javafx.geometry.Insets(20, 0, 0, 0)); // Margin for text
        StackPane.setMargin(name, new javafx.geometry.Insets(60, 0, 0, 0)); // Margin for text
        StackPane.setMargin(continueButton, new javafx.geometry.Insets(0, 0, 20, 0)); // Margin for button

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/welcome-style.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Witamy w aplikacji słownikowej!");
        stage.show();
    }
}
