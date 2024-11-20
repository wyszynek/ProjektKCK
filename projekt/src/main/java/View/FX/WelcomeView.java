package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

        continueButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().add(continueButton);
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Witamy w aplikacji słownikowej!");
        stage.show();
    }
}
