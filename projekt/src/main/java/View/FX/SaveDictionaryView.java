package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SaveDictionaryView {
    private Stage stage;
    private DictionaryControllerFX controller;

    public SaveDictionaryView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label instructionLabel = new Label("Podaj nazwę pliku, do którego chcesz zapisać słownik:");
        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Nazwa pliku (np. slownik.txt)");

        Button saveButton = new Button("Zapisz");
        Button backButton = new Button("Powrót");

        saveButton.setOnAction(e -> {
            String fileName = fileNameField.getText().trim();
            if (!fileName.isEmpty()) {
                controller.saveDictionaryToFile(fileName);
                fileNameField.clear(); // Wyczyść pole tekstowe po zapisaniu
            } else {
                instructionLabel.setText("Nazwa pliku nie może być pusta!");
            }
        });

        backButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().addAll(instructionLabel, fileNameField, saveButton, backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Zapisz słownik");
    }
}
