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

        feedbackLabel = new Label();  // Informacja zwrotna dla użytkownika

        // Tworzymy elementy UI tylko raz
        wordField = new TextField();
        wordField.setPromptText("Słówko");

        translationField = new TextField();
        translationField.setPromptText("Tłumaczenie");

        saveButton = new Button("Dodaj słówko");
        backButton = new Button("Powrót");
    }

    public void show() {
        feedbackLabel.setVisible(false);    // Ukrywamy przycisk "Przejdź do następnego"
        saveButton.setOnAction(e -> {
            if(!wordField.getText().isEmpty() && !translationField.getText().isEmpty()) {
                if(controller.checkIfExists(wordField.getText(), translationField.getText())) {
                    feedbackLabel.setVisible(true);
                    feedbackLabel.setText("Pomyślnie dodano słówko.");
                    controller.addWord(wordField.getText(), translationField.getText());
                    clearFields();  // Czyścimy tylko pola tekstowe
                }
                else {
                    feedbackLabel.setVisible(true);
                    feedbackLabel.setText("Podane słowa już istnieją.");
                }
            }
            else {
                feedbackLabel.setVisible(true);
                feedbackLabel.setText("Proszę wypełnić puste pola.");
            }
        });

        backButton.setOnAction(e -> {
            controller.showMainMenu();
            clearFields();  // Czyścimy tylko pola tekstowe
        });

        layout.getChildren().addAll(feedbackLabel, wordField, translationField, saveButton, backButton);

        // Scena jest ustawiana tylko raz
        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/addword-style.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Dodaj słówko");
    }

    // Dodatkowa metoda do czyszczenia tylko zmienionych elementów
    private void clearFields() {
        wordField.clear();      // Czyszczenie pola tekstowego dla słówka
        translationField.clear();  // Czyszczenie pola tekstowego dla tłumaczenia
    }
}
