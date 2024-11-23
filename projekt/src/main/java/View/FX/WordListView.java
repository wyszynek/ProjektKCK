package View.FX;

import Controller.DictionaryControllerFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        // Przycisk "Usuń" (początkowo nieaktywny)
        Button deleteButton = new Button("Usuń");
        deleteButton.setDisable(true); // Początkowo przycisk jest zablokowany

        // Aktywacja przycisku "Usuń" po zaznaczeniu słowa
        wordList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteButton.setDisable(false); // Aktywuj przycisk, gdy wybrane jest słowo
            }
        });

        // Akcja usuwania słowa po kliknięciu "Usuń"
        deleteButton.setOnAction(e -> {
            String selectedWord = wordList.getSelectionModel().getSelectedItem();
            String wordToDelete = selectedWord.split(" - ")[0]; // Wyciągamy słowo przed myślnikiem

            // Usuwamy słowo z słownika
            controller.removeWord(wordToDelete);

            // Usuwamy je z ObservableList, co spowoduje automatyczne odświeżenie widoku listy
            words.remove(selectedWord);

            deleteButton.setDisable(true); // Zablokuj przycisk po usunięciu słowa
        });

        Button backButton = new Button("Powrót");
        backButton.setOnAction(e -> controller.showMainMenu());

        layout.getChildren().addAll(searchField, wordList, deleteButton, backButton);

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Lista słówek");
    }
}
