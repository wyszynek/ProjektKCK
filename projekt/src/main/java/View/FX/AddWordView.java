package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class AddWordView {
    private Stage stage;
    private DictionaryControllerFX controller;

    private VBox layout;
    private TextField wordField;
    private TextField translationField;
    private Button saveButton;
    private Button backButton;
    private Label feedbackLabel;

    public AddWordView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        feedbackLabel = new Label();

        wordField = new TextField();
        wordField.setPromptText("Słówko");

        translationField = new TextField();
        translationField.setPromptText("Tłumaczenie");

        saveButton = new Button("Dodaj słówko");
        backButton = new Button("Powrót");
    }

    public void show() {
        feedbackLabel.setVisible(false);

        saveButton.setOnAction(e -> {
            String word = wordField.getText();
            String translation = translationField.getText();

            // Delegowanie logiki do kontrolera
            String feedback = controller.handleAddWord(word, translation);

            // Inteligentne odświeżanie
            feedbackLabel.setText(feedback);
            feedbackLabel.setVisible(true);

            if ("Pomyślnie dodano słówko.".equals(feedback)) {
                clearFields();
            }
        });

        backButton.setOnAction(e -> {
            controller.showMainMenu();
            clearFields();
        });

        layout.getChildren().addAll(feedbackLabel, wordField, translationField, saveButton, backButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Dodaj słówko");
    }

    private void clearFields() {
        wordField.clear();
        translationField.clear();
    }
}
