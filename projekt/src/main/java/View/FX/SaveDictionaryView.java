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

    private VBox layout;
    private Label instructionLabel;
    private TextField fileNameField;
    private Button saveButton;
    private Button backButton;

    public SaveDictionaryView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        instructionLabel = new Label("Podaj nazwę pliku, do którego chcesz zapisać słownik:");
        fileNameField = new TextField();
        fileNameField.setPromptText("Nazwa pliku (np. slownik.txt)");

        saveButton = new Button("Zapisz");
        backButton = new Button("Powrót");
    }

    public void show() {
        saveButton.setOnAction(e -> {
            String fileName = fileNameField.getText().trim();
            if (!fileName.isEmpty()) {
                controller.saveDictionaryToFile(fileName);
                instructionLabel.setText("Zapisano słówka do pliku " + fileName);
                fileNameField.clear(); // Wyczyść pole tekstowe po zapisaniu
            } else {
                instructionLabel.setText("Nazwa pliku nie może być pusta!");
            }
        });

        backButton.setOnAction(e -> {
            controller.showMainMenu();
            clearFields(); // Czyści pola tekstowe i resetuje komunikaty
        });

        layout.getChildren().addAll(instructionLabel, fileNameField, saveButton, backButton);

        Scene scene = new Scene(layout, 500, 400);
        stage.setScene(scene);
        stage.setTitle("Zapisz słownik");
    }

    private void clearFields() {
        fileNameField.clear();
        instructionLabel.setText("Podaj nazwę pliku, do którego chcesz zapisać słownik:");
    }
}
