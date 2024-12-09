package View.FX;

import Controller.DictionaryControllerFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

public class WordListView {
    private Stage stage;
    private DictionaryControllerFX controller;
    private ObservableList<String> words;
    private ListView<String> wordList;

    public WordListView(Stage stage, DictionaryControllerFX controller, Map<String, String> dictionary) {
        this.stage = stage;
        this.controller = controller;

        // Tworzenie listy słów na podstawie przekazanej mapy
        this.words = FXCollections.observableArrayList(
                dictionary.entrySet().stream()
                        .map(entry -> entry.getKey() + " - " + entry.getValue())
                        .collect(Collectors.toList())
        );
    }

    public void show() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        TextField searchField = new TextField();
        searchField.setPromptText("Wyszukaj słówko");

        wordList = new ListView<>();
        wordList.setItems(words);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            wordList.setItems(FXCollections.observableArrayList(
                    words.stream()
                            .filter(word -> word.toLowerCase().contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            ));
        });

        Button deleteButton = new Button("Usuń");
        deleteButton.setDisable(true);

        wordList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteButton.setDisable(false);
            }
        });

        deleteButton.setOnAction(e -> {
            String selectedWord = wordList.getSelectionModel().getSelectedItem();
            String wordToDelete = selectedWord.split(" - ")[0];

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Potwierdzenie");
            alert.setHeaderText("Na pewno chcesz usunąć to słowo?");
            alert.setContentText("Słowo: " + wordToDelete);

            // Czekaj na odpowiedź użytkownika
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Usuwamy słowo z słownika
                    controller.removeWord(wordToDelete);

                    // Usuwamy je z ObservableList, co spowoduje automatyczne odświeżenie widoku listy
                    words.remove(selectedWord);

                    deleteButton.setDisable(true); // Zablokuj przycisk po usunięciu słowa
                }
            });
        });

        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().addAll(searchField, wordList, deleteButton, backButton);

        Scene scene = new Scene(layout, 500, 400);

        URL resource = getClass().getResource("/styles.css");
        if (resource != null) {
            scene.getStylesheets().add(resource.toExternalForm());
        } else {
            System.out.println("CSS file not found!");
        }

        stage.setScene(scene);
        stage.setTitle("Lista słówek");
    }
}
