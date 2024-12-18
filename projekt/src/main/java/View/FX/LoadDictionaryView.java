package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class LoadDictionaryView {
    private Stage stage;
    private DictionaryControllerFX controller;

    private VBox layout;
    private Label instructionLabel;
    private TextField fileNameField;
    private Button loadButton;
    private Button backButton;

    public LoadDictionaryView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        instructionLabel = new Label("Podaj nazwę pliku, z którego chcesz wczytać słownik:");
        fileNameField = new TextField();
        fileNameField.setPromptText("Nazwa pliku (np. slownik.txt)");

        loadButton = new Button("Wczytaj");
        backButton = new Button("Powrót");
    }

    public void show() {
        loadButton.setOnAction(e -> {
            String fileName = fileNameField.getText().trim();
            if (!fileName.isEmpty()) {
                File file = new File(fileName);

                if (file.exists() && file.isFile()) {
                    controller.loadDictionaryFromFile(fileName);
                    instructionLabel.setText("Pomyślnie wczytano słówka z " + fileName);
                    fileNameField.clear();
                } else {
                    instructionLabel.setText("Nieprawidłowa nazwa pliku lub plik nie istnieje!");
                }
            } else {
                instructionLabel.setText("Nazwa pliku nie może być pusta!");
            }
        });

        backButton.setOnAction(e -> {
            controller.showMainMenu();
            clearFields();
        });

        // Ustawienie sceny i tytułu
        if (stage.getScene() == null) {
            stage.setScene(new javafx.scene.Scene(layout, 400, 300));
        }

        layout.getChildren().addAll(instructionLabel, fileNameField, loadButton, backButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Wczytaj słownik");
    }

    // Metoda do dynamicznego czyszczenia pól tekstowych i przywracania domyślnego komunikatu
    private void clearFields() {
        fileNameField.clear();
        instructionLabel.setText("Podaj nazwę pliku, z którego chcesz wczytać słownik:");
    }
}

