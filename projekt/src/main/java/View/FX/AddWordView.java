package View.FX;

import Controller.DictionaryControllerFX;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddWordView {
    private Stage stage;
    private DictionaryControllerFX controller;

    private TextField wordField;
    private TextField translationField;
    private Button saveButton;
    private Button backButton;

    public AddWordView(Stage stage, DictionaryControllerFX controller) {
        this.stage = stage;
        this.controller = controller;

        // Tworzymy elementy UI tylko raz
        wordField = new TextField();
        wordField.setPromptText("Słówko");

        translationField = new TextField();
        translationField.setPromptText("Tłumaczenie");

        saveButton = new Button("Dodaj słówko");
        backButton = new Button("Powrót");
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        saveButton.setOnAction(e -> {
            // Dodajemy słowo i tłumaczenie
            controller.addWord(wordField.getText(), translationField.getText());
            clearFields();  // Czyścimy tylko pola tekstowe
        });

        backButton.setOnAction(e -> {
            controller.showMainMenu();
            clearFields();  // Czyścimy tylko pola tekstowe
        });

        layout.getChildren().addAll(wordField, translationField, saveButton, backButton);

        // Scena jest ustawiana tylko raz
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Dodaj słówko");
    }

    // Dodatkowa metoda do czyszczenia tylko zmienionych elementów
    private void clearFields() {
        wordField.clear();      // Czyszczenie pola tekstowego dla słówka
        translationField.clear();  // Czyszczenie pola tekstowego dla tłumaczenia
    }
}
